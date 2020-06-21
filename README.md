# TelegramBot FoodSharing

### Описание

Телеграм бот для поиска объявлений FoodSharing c возможностью указать геолокацию.



### Требования к software:
1. Java 1.8
2. Maven 3.8.0

### Технологический стек:
1. Mybatis 3.5.4
2. Postgresql 42.2.14

### Имя и контакты разработчика

Anastasia Kolevatykh

anastasia.kolevatykh@gmail.com

### Команды для сборки приложения:
```
mvn clean install
```
### Команды для запуска приложения:
```
java -jar target/telegrambot/bin/telegrambot.jar 
```

### Команды для создания таблиц в базе данных:

```
CREATE TABLE app_user (
id varchar(255) NOT NULL,
chatId varchar(255) DEFAULT NULL,
userName varchar(255) DEFAULT NULL,
firstName varchar(255) DEFAULT NULL,
lastName varchar(255) DEFAULT NULL,
location varchar(255) DEFAULT NULL,
PRIMARY KEY (id)
) ;
```