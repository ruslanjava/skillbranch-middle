package ru.skillbranch.sbdelivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.skillbranch.sbdelivery.screens.root.logic.Command
import ru.skillbranch.sbdelivery.screens.root.logic.NavigateCommand
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme
import ru.skillbranch.sbdelivery.screens.root.ui.RootScreen

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    private val vm : RootViewModel by viewModels()

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            vm.dispatcher.androidCommands
                .collect (::handleCommands)
        }

        setContent {
            AppTheme {
                RootScreen(vm = vm)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        vm.saveState()
        super.onSaveInstanceState(outState)
    }

    private fun handleCommands(cmd: Command){
        when(cmd){
            Command.Finish -> finish()
        }
    }

    override fun onBackPressed() {
        vm.navigate(NavigateCommand.ToBack)
    }
}

