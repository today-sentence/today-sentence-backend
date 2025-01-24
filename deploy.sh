#!/bin/bash

# 사용자 입력
JAR_FILE=$1   # JAR 파일 경로
EC2_USER=$2                      # EC2 사용자 이름
EC2_IP=$3  # EC2 퍼블릭 DNS
REMOTE_PATH="/home/ubuntu"  # EC2에서 파일을 저장할 경로
APP_NAME="today-sentence-0.0.1-SNAPSHOT.jar"  # EC2에 저장될 파일 이름
KEY_PATH=$4

# 1. JAR 파일 업로드
# echo "Uploading $JAR_FILE to $EC2_USER@$EC2_IP:$REMOTE_PATH"
# scp -i "$KEY_PATH" "$JAR_FILE" "$EC2_USER@$EC2_IP:$REMOTE_PATH/$APP_NAME"
# if [ $? -ne 0 ]; then
#   echo "File upload failed. Please check the JAR file path or EC2 connection."
#   exit 1
# fi

# 1. PEM 키 경로 확인
if [ ! -f "$KEY_PATH" ]; then
  echo "PEM key not found: $KEY_PATH"
  exit 1
fi

# 2. EC2 접속 및 애플리케이션 종료
echo "Connecting to EC2 and stopping existing application..."
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_IP" << EOF
  pkill -f "$APP_NAME" || echo "No running application to stop."
EOF
if [ $? -ne 0 ]; then
  echo "Failed to stop existing application. Please check the EC2 connection."
  exit 1
fi

# 3. 새 애플리케이션 실행
echo "Starting new application..."
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_IP" << EOF
  nohup java -jar "$REMOTE_PATH/$APP_NAME" > "$REMOTE_PATH/app.log" 2>&1 &
EOF
if [ $? -ne 0 ]; then
  echo "Failed to start the new application. Please check the JAR file or Java environment on EC2."
  exit 1
fi

# 4. 로그 출력
echo "Deployment completed. Showing logs:"
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_IP" "tail -n 20 $REMOTE_PATH/app.log"
if [ $? -ne 0 ]; then
  echo "Failed to retrieve logs. Please check the EC2 connection or log file path."
  exit 1
fi

# 5.끝
