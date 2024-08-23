
# Building the Pera Wallet Android App

After you clone or download this repo to your computer, you can run the project right away without any additional setup.

## Optional Configurations

1.  **Firebase account:** The project includes a `google-services.json` file connected to a demo Firebase project. If you want to link your own Firebase project, you can replace the `google-services.json` file with yours.

2. **Node and indexer:** The project already has [Nodely](https://nodely.io/docs/free/start) public API keys and URLs configured for the node and indexer. To set up a different indexer or node, you can follow these steps:

2.1 **Create a new node:**  The apps need access to instances of [Algod](https://github.com/algorand/go-algorand) and
[Indexer](https://github.com/algorand/indexer) to run. The official app supports MainNet
and TestNet; however, any network can be used if you supply your node. For a private network,
the quickest way to get started is by using the [Algorand sandbox](https://github.com/algorand/sandbox).
[This page from the Algorand developer docs](https://developer.algorand.org/docs/build-apps/setup/#how-do-i-obtain-an-algod-address-and-token)
contains more options. Regardless of how you obtain access to instances of Algod and Indexer, you
will need their addresses and API keys to continue.

2.2. **Define network access tokens:** In order to tell the app how to access Algod and Indexer, you
will need to update the keys in the `api-key.properties` files. You can find these files in two places:
`android/app/src/prod/api-key.properties` and `android/app/src/staging/api-key.properties`. You need to
define two values, `ALGORAND_API_KEY` and `INDEXER_API_KEY`, which are the API tokens
for Algod and Indexer, respectively.

2.3. **Specify network addresses:** You will need to change the default addresses for Algod and Indexer
in this file: `android/app/src/main/api-url.properties`. The variables to change are `NODE_MAINNET_URL`, `NODE_TESTNET_URL`, `INDEXER_MAINNET_URL`, and `INDEXER_TESTNET_URL` for the MainNet and TestNet.
For example, if your Algod address is `http://localhost:4001` and your Indexer address is `http://localhost:8980`,
the following changes need to be made for the MainNet variables (the TestNet variables can be
similarly changed):

```diff
-     NODE_MAINNET_URL="https://mainnet-api.algonode.cloud/"
+     NODE_MAINNET_URL="http://localhost:4001"
      NODE_TESTNET_URL="https://testnet-api.algonode.cloud/"
-     INDEXER_MAINNET_URL="https://mainnet-idx.algonode.cloud/"
+     INDEXER_MAINNET_URL="http://localhost:8980"
      INDEXER_TESTNET_URL="https://testnet-idx.algonode.cloud/"
```

> Note: it's okay to use a localhost address in the emulator, but that address will not work if you deploy to an actual device.

2.4. **Build the app:** Once all the above steps are complete, you are ready to build and deploy the
Android app.

