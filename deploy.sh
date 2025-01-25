#!/bin/bash

# 사용자 입력

JAR_FILE=$1   # JAR 파일 이름
EC2_USER=$2                      # EC2 사용자 이름
EC2_IP=$3    # EC2 퍼블릭 DNS
KEY_PEM=$4
REMOTE_PATH="/home/ubuntu"  # EC2에서 파일을 저장할 경로
APP_NAME=$JAR_FILE  # EC2에 저장될 파일 이름

# 1. 수행 중인 애플리케이션 pid 확인
CURRENT_PID=${pgrep -f $JAR_FILE})

echo "현재 실행 중인 $JAR_FILE 의 pid : $CURRENT_PID "


# 2. 애플리케이션 종료
if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 5
fi

# 3. 새 애플리케이션 실행
echo "Starting new application..."
nohup java -jar "$REMOTE_PATH/$JAR_FILE" > "$REMOTE_PATH/app.log" 2>&1 &

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
