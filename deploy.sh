#!/bin/bash

# 사용자 입력
EC2_USER=$1           # EC2 사용자 이름
EC2_IP=$2             # EC2 퍼블릭 DNS 또는 IP 주소
KEY_PEM=$3            # .pem 키 파일 경로
REMOTE_PATH="/home/ubuntu"  # EC2에서 파일을 저장할 경로
APP_NAME="today-sentence-0.0.1-SNAPSHOT.jar"  # EC2에서 실행될 파일 이름

# 1. EC2에서 기존 애플리케이션 종료
echo "Connecting to EC2 and stopping existing application..."
ssh -i "$KEY_PEM" "$EC2_USER@$EC2_IP" << EOF
  pkill -f "$APP_NAME" || echo "No running application to stop."
EOF
if [ $? -ne 0 ]; then
  echo "Failed to stop existing application. Please check the EC2 connection."
  exit 1
fi

# 2. 새 애플리케이션 실행
echo "Starting new application on EC2..."
ssh -i "$KEY_PEM" "$EC2_USER@$EC2_IP" << EOF
  nohup java -jar "$REMOTE_PATH/$APP_NAME" > "$REMOTE_PATH/app.log" 2>&1 &
EOF
if [ $? -ne 0 ]; then
  echo "Failed to start the new application. Please check the JAR file or Java environment on EC2."
  exit 1
fi

# 3. 로그 출력
echo "Deployment completed. Showing logs:"
ssh -i "$KEY_PEM" "$EC2_USER@$EC2_IP" "tail -n 20 $REMOTE_PATH/app.log"
if [ $? -ne 0 ]; then
  echo "Failed to retrieve logs. Please check the EC2 connection or log file path."
  exit 1
fi

# 끝
echo "Deployment finished successfully."
