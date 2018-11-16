
[![Build Status](https://travis-ci.org/Kvitral/scala-money-store.svg?branch=master)](https://travis-ci.org/Kvitral/scala-money-store)
# scala-money-store
**F[_] for Funky**

## Motivation
Simple petstore that tries to leverage the Tagless Final approach (or sort of).

### API

#### getAccounts
*Path*: /getAccounts

  Parameter | Description
------------ | -------------
accountId | Id of account


#### transfer
*Path*: /getAccounts

*Entity Type* : JSON

  Parameter | Description
------------ | -------------
from | Id of account
to | Id of account
amount | money amount in double
currency | currency of transaction
