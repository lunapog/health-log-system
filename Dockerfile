# 第一階段：使用 Maven 進行編譯與打包
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# 將本地原始碼複製到容器中
COPY . .
# 執行打包，跳過測試以求極速
RUN mvn clean package -DskipTests

# 第二階段：使用輕量級 JRE 執行程式
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 從編譯階段將產出的 jar 檔複製過來
COPY --from=build /app/target/*.jar app.jar

# 設定預設 Port
EXPOSE 8080

# 啟動命令
ENTRYPOINT ["java", "-jar", "app.jar"]