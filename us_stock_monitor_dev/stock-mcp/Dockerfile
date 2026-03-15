# 使用 Eclipse Temurin JDK 21 作为基础镜像
FROM eclipse-temurin:21-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制打包好的 JAR 文件
COPY target/stock-mcp-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 7070

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
