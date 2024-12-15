# Stock manager - protocol specification

This application's protocol is mostly a proxy for PostgreSQL queries.

## Insertable types
These main data types are registered to be used in the application's various relation types.

- product
- shelf
- supplier
- year_segment

## Value insertion
`PUT <insertable type> [values...]`

**Note:** The values inserted in the row must correspond to the fields of the table.

## Commands
`SELL <product_code> <date> <n>`

Adds n sold products for a given date. \
n can be negative.

`THROW <product_code> <date> <n>`

Adds n unsold products for a given date.

`RECEIVE <product_code> <n> <reception_date> <expiration_date>`

Registers the reception of n units of a product.

`SHELF <product_code> <shelf_id>`

Put a product on a shelf.

`ORDER INFO <product_code> <segment_id> <target> <threshold>`

Modifies the order information for a given year segment for a product.