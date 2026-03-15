# 使用 Ubuntu 作为基础镜像，安装 OpenJDK 21
FROM docker.1ms.run/ubuntu/squid:latest

# 安装 OpenJDK 21
RUN apt-get update && \
    apt-get install -y openjdk-21-jre-headless && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制打包好的 JAR 文件
COPY target/stock-mcp-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 7070

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
