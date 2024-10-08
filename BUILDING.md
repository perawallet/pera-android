
# Building the Pera Wallet Apps

Some modifications are needed to build the applications from this repo. This document
explains all the steps needed to build and run the applications in a simulator.

## Common Steps

1. **Clone this repo:** Clone or download this repo to your computer.

2. **Obtain network access:** The apps need access to instances of [Algod](https://github.com/algorand/go-algorand) and
[Indexer](https://github.com/algorand/indexer) to run. The official app supports MainNet
and TestNet; however, any network can be used if you supply your node. For a private network,
the quickest way to get started is using the [Algorand sandbox](https://github.com/algorand/sandbox).
[This page from the Algorand developer docs](https://developer.algorand.org/docs/build-apps/setup/#how-do-i-obtain-an-algod-address-and-token)
contains more options. Regardless of how you obtain access to instances of Algod and Indexer, you
will need their addresses and API keys to continue.

3. **Create a Firebase project:** Since both applications use Firebase, you will need to create your own
Firebase project for them to run. See [the Firebase docs](https://firebase.google.com/docs/projects/learn-more#setting_up_a_firebase_project_and_registering_apps)
for further instructions.

After these steps are complete, you can move on to specific steps for Android.

## Android Steps

You can run the project right away without any additional setup. It includes a `google-services.json` file connected to a demo Firebase project. If you want to link your own Firebase project, refer to Step 1. The project already has [Nodely](https://nodely.io/docs/free/start) public API keys and URLs configured for the node and indexer. To set up a different indexer or node, follow the instructions in Steps 2 and 3.

1. **Download the Android Firebase config file:** The Firebase config file for Android is called `google-services.json`.
See this link for how to obtain the config file: https://support.google.com/firebase/answer/7015592.
Once you've downloaded it, place it in the location `android/app/google-services.json`.

2. **Define network access tokens:** In order to tell the app how to access Algod and Indexer, you
will need to update the keys in the `api-key.properties` files. You can find these files in two places:
`android/app/src/prod/api-key.properties` and `android/app/src/staging/api-key.properties`. You need to
 define two values, `ALGORAND_API_KEY` and `INDEXER_API_KEY`, which are the API tokens
for Algod and Indexer, respectively. 

3. **Specify network addresses:** You will need to change the default addresses for Algod and Indexer
in this file: `android/app/src/main/java/com/algorand/android/utils/Nodes.kt`. The variables to change are `algodAddress` and `indexerAddress` for the MainNet and TestNet objects.
For example, if your Algod address is `http://localhost:4001` and your Indexer address is `http://localhost:8980`,
the following changes need to be made for the MainNet variables (the TestNet variables can be
similarly changed):

```diff
     Node(
         name = "MainNet",
-        algodAddress = "https://node-mainnet.aws.algodev.network/",
+        algodAddress = "http://localhost:4001",
         algodApiKey = BuildConfig.ALGORAND_API_KEY,
-        indexerAddress = "https://indexer-mainnet.aws.algodev.network/",
+        indexerAddress = "http://localhost:8980",
         indexerApiKey = BuildConfig.INDEXER_API_KEY,
         isActive = true,
         isAddedDefault = true,
         networkSlug = MAINNET_NETWORK_SLUG
     ),
```

> Note: it's ok to use a localhost address in the simulator, but that address will not work if you deploy to an actual device.

4. **Build the app:** Once all the above steps are complete, you are ready to build and deploy the
Android app.
