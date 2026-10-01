# syntax=docker/dockerfile:1
# RePhone 后端多阶段构建：源码进镜像，docker compose up -d --build 一条命令出可运行服务。
# 依赖通过 BuildKit 缓存挂载持久化（首次拉取后不再重复下载）。

# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
# 阿里云 Maven 镜像（国内网络加速）；如无需可删掉 -s 参数
COPY docker/mvn-settings.xml /root/.m2/settings.xml
COPY . .
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn -q -B package -DskipTests

# ---- 运行阶段 ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/start/target/rephone-server.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
