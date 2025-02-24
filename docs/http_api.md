# HTTP API specification

This piece of documentation explains the backend's
HTTP endpoints.

## /api/products

`GET /api/products/all`

Lists all the products registered in the database in json format.

Always returns a json array, which may or may not be empty.

`GET /api/products/{code}`

Displays the information of the product identified by the given code.

Returns an error 404 if no product exists with the given product code.

`GET /api/products/expired`

Lists all the products that have an expiry date less than the current date.

Always returns a json array, which may or may not be empty.

`GET /api/products/soonExpired`

Lists all the products that have an expiry date less than 7 days after the current date.

Always returns a json array, which may or may not be empty.

`GET /api/products/orders`

Lists the orders for all products.

Always returns a json array, which may or may not be empty.

`POST /api/products`

Insert a product based on a provided json object, which conforms to the pattern below:

```json
{
  "code": "12345678",
  "name": "name",
  "price": "5.65",
  "supplierId": "2"
}
```

The product code is a 64-bit integer, and the supplierId must correspond to an existing supplier in the database.

Can return an error 400 if the input does not conform to this scheme.

`DELETE /api/products/{code}`

Deletes the product with the specified code.

Returns an error 404 if no product exists in the database with the given code.

