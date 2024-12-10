/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.keyreg.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionPreview

@Preview
@SuppressLint("ComposableNaming")
@Composable
fun keyRegTable(
    keyRegTransactionDetail: KeyRegTransactionPreview? = null,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    if (keyRegTransactionDetail != null) {
        val itemsList = listOf(
            "Address" to keyRegTransactionDetail.address,
            "Fee" to keyRegTransactionDetail.fee,
            "Type" to keyRegTransactionDetail.type,
            "Selection Key" to keyRegTransactionDetail.selectionKey,
            "Voting Key" to keyRegTransactionDetail.votingKey,
            "State Proof Key" to keyRegTransactionDetail.stateProofKey,
            "First Valid Round" to keyRegTransactionDetail.firstValid,
            "Last Valid Round" to keyRegTransactionDetail.lastValid,
            "xNote" to keyRegTransactionDetail.xNote,
            "Note" to keyRegTransactionDetail.note,
            "Signing Address" to keyRegTransactionDetail.signingAddress,
        )

        val fabHeight by remember {
            mutableStateOf(0)
        }

        val heightInDp = with(LocalDensity.current) { fabHeight.toDp() }

        Scaffold(
            topBar = {
                scaffoldTopAppBar(onBackClick)
            },
            floatingActionButton = {
                algorandButton("Confirm Transaction", onConfirmClick)
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {
            contentPadding ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 75.dp, bottom = heightInDp + 100.dp),
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                ) {
                items(itemsList) { item ->
                    keyRegTableRowItem(item.first, item.second.toString())
                }
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
        ) {
            Text(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                text = "No Key Reg Transaction"
            )
        }
    }
}

@Preview
@SuppressLint("ComposableNaming")
@Composable
fun keyRegTableRowItem(key: String = "", value: String? = "") {
    Row(
        modifier =
        Modifier
            .width(327.dp)
            .wrapContentHeight()
            .padding(5.dp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier =
            Modifier
                .fillMaxHeight()
                .width(100.dp),
        ) {
            Text(
                color = Color.DarkGray,
                modifier =
                Modifier
                    .padding(end = 27.dp),
                text = key
            )
        }
        Spacer(modifier = Modifier.width(27.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier =
            Modifier
                .fillMaxHeight()
                .background(Color.White)
                .width(200.dp),
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier =
                Modifier
                    .padding(end = 25.dp),
                text = value.orEmpty()
            )
        }
    }

    when (key) {
        "Type", "Key Dilution", "Last Valid Round", "Note" -> {
            Spacer(Modifier.height(5.dp))
            algorandDivider()
            Spacer(Modifier.height(5.dp))
        }
    }
}
