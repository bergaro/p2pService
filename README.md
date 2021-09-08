# Сервис перевода денег

## _Описание взаимодействия_

Схема логики работы приложения расположена
в корне проекта, в двух вариантах (*.pdf, *.vsdx):
```
applicationScheme.vsdx
applicationScheme.pdf
```

Фронт используемый при разработке: https://serp-ya.github.io/card-transfer/

### 1) Transfer request

Запрос создаёт операцию перевода денежных средств.
Перевод не осуществляется до получения акцепта(_confirm operation_) от фронта.
```
POST http://localhost:5500/transfer
Content-Type: application/json
{
  "cardFromNumber": "1111111111111111",
  "cardFromValidTill": "11/22",
  "cardFromCVV": "123",
  "cardToNumber": "2222222222222222",
  "amount": {
    "value": 100,
    "currency": "810"
  }
}
```

### 2) Transfer response

В ответе на запрос transfer request, в случае
успешного создания расходной операции, сервер 
возвращает на фронт GUID транзакции.

Под полученным GUID на сервере хранится, не подтверждённая
фронтом, операция

```
POST http://localhost:5500/transfer

HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 08 Sep 2021 13:49:52 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "operationId": "a4a03ef3-10ab-11ec-a321-b3e5fecfb39c"
}

Response code: 200; Time: 259ms; Content length: 54 bytes
```

### 3) Генерируемые сервером отказы

#### 3.1) Error input data

Если не корректно заполнено сообщение(transfer),
то сервер ответит 400-кодом отказа.

_Например:_
Карта отправителя == Карте получателя

```
POST http://localhost:5500/transfer

HTTP/1.1 400 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: text/plain;charset=UTF-8
Content-Length: 18
Date: Wed, 08 Sep 2021 14:01:31 GMT
Connection: close

Invalid input data

Response code: 400; Time: 371ms; Content length: 18 bytes

```

#### 3.2) Error transfer

Если сервер не может произвести операцию
по другим причинам, то на фронт придёт 
500-код отказа.

_Например:_ (Сумма перевод + комиссия) меньше
чем находится на счету отправителя.

```
POST http://localhost:5500/transfer

HTTP/1.1 500 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: text/plain;charset=UTF-8
Content-Length: 16
Date: Wed, 08 Sep 2021 14:05:31 GMT
Connection: close

Not enough money

Response code: 500; Time: 221ms; Content length: 16 bytes
```

### 4) Confirm operation request

Запрос является подтвержением операции перевода
создаваемой transferRequest.

```
POST http://localhost:5500/confirmOperation
Content-Type: application/json
{
  "code": "0000",
  "operationId": "a4a03ef3-10ab-11ec-a321-b3e5fecfb39c"
}

```

### 5) Confirm operation response

Ответ на запрос confirmOperation, 
сигнализирующий о успешном переводе 
денежных средств.

```
POST http://localhost:5500/confirmOperation

HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 08 Sep 2021 14:13:43 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "operationId": "870924d4-10ae-11ec-a321-6f67bca2faf1"
}

Response code: 200; Time: 367ms; Content length: 54 bytes
```

### 6) Генерируемые сервером отказы

#### 6.1) Error input data

Если во входящем json не заполнен какой-либо
атрибут, сервер ответит 400-кодом отказа.

_Например:_ Поле code и/или operationId == null

```
POST http://localhost:5500/confirmOperation

HTTP/1.1 400 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: text/plain;charset=UTF-8
Content-Length: 35
Date: Wed, 08 Sep 2021 14:17:40 GMT
Connection: close

Code or operationId may not be null

Response code: 400; Time: 131ms; Content length: 35 bytes

```

#### 6.2) Error transfer

Если сервер не может произвести операцию 
по другим причинам, то на фронт придёт 
500-код отказа.

_Например:_ не акцептованая операция не найдена.

```
POST http://localhost:5500/confirmOperation

HTTP/1.1 500 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Content-Type: text/plain;charset=UTF-8
Content-Length: 14
Date: Wed, 08 Sep 2021 14:19:52 GMT
Connection: close

Incorrect GUID

Response code: 500; Time: 323ms; Content length: 14 bytes

```
