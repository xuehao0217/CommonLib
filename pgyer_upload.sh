#!/bin/bash

# 设置蒲公英平台的API Key和应用ID
API_KEY="your_api_key"
APP_ID="your_app_id"

# 获取当前模式
MODE="$1"

# 检查模式是否有效
if [[ "$MODE" != "debug" && "$MODE" != "release" && "$MODE" != "online" ]]; then
  echo "无效的模式：$MODE"
  echo "请使用以下模式之一：debug, release, online"
  exit 1
fi

# 使用Gradle构建工具打包应用
./gradlew assemble$MODE

# 签名应用
./gradlew signingReport

# 上传应用包到蒲公英平台
if [[ "$MODE" == "release" || "$MODE" == "online" ]]; then
  curl -F "file=@app/build/outputs/apk/$MODE/app-$MODE.apk" -F "uKey=$API_KEY" -F "_api_key=$API_KEY" https://www.pgyer.com/apiv2/app/upload
fi

# 输出应用路径
echo "应用已打包并签名，路径为：app/build/outputs/apk/$MODE/app-$MODE.apk"





#sh pgyer_upload.sh debug
#sh pgyer_upload.sh release
#sh pgyer_upload.sh online