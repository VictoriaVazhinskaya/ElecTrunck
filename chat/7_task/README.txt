POST запрос:
POST /chat HTTP/1.1
Host: localhost:999
User-Agent: Fiddler

{"id":"77777777", "user":"User66", "message":"What's up?"}


/----------------------------/


DELETE запрос:
DELETE /chat?(id) HTTP/1.1
Host: localhost:999
User-Agent: Fiddler


/----------------------------/


PUT запрос:
PUT /chat HTTP/1.1
Host: localhost:999
User-Agent: Fiddler

{"id":"190372890", "message":"What's up?"}