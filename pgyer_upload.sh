#!/bin/bash

# Pgyer API key
PGYER_API_KEY="3623f086318dbb911bb39e5b42a39d11"

# Pgyer user key
PGYER_USER_KEY="e0594cc27039c10e449ba15d16ec3ab8"

# 默认构建类型为 debug，可通过命令行参数覆盖
BUILD_TYPE=${1:-debug}

# 应用名称
APP_NAME="commonlib"

# 询问用户选择构建类型
read -p "请选择构建类型（输入1为debug，2为release）: " BUILD_TYPE_CHOICE

case $BUILD_TYPE_CHOICE in
  1)
    BUILD_TYPE="debug"
    ;;
  2)
    BUILD_TYPE="release"
    ;;
  *)
    echo "无效输入，将使用默认值debug。"
    BUILD_TYPE="debug"
    ;;
esac

# 提示用户输入更新描述，允许直接回车跳过输入
read -p "请输入更新描述（可选）: " updateDescription
updateDescription=${updateDescription:-"默认更新描述"}

# 构建应用
echo "开始构建 ${BUILD_TYPE} 版本..."
./gradlew assemble${BUILD_TYPE}

# 设置APK文件所在目录
APK_DIR="./app/build/outputs/apk/$BUILD_TYPE/"

# 查找APK文件并上传到蒲公英
APK_FILE=$(find "$APK_DIR" -maxdepth 1 -name "*.apk" 2>/dev/null)

if [ ! -f "$APK_FILE" ]; then
  echo "未找到APK文件!"
  exit 1
fi

echo "构建完成 ${APK_FILE}，开始上传到蒲公英..."

# 上传至蒲公英并传递更新描述
curl -F "file=@$APK_FILE" -F "uKey=$PGYER_USER_KEY" -F "_api_key=$PGYER_API_KEY" -F "updateDescription=$updateDescription" https://www.pgyer.com/apiv1/app/upload
echo
echo "---------------------上传完成---------------------"