package com.example.project

import Data.BottomNavItem
import Language.Language
import ViewModels.Setting
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState




@Composable
fun EditInputField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
)
{
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        singleLine = true,
        keyboardOptions=keyboardOptions,
        label = { Text(stringResource(label)) },
    )
}

@Composable
fun PasswordInputField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
)
{
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        singleLine = true,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        modifier=modifier,
        visualTransformation =
        if (passwordVisible) PasswordVisualTransformation()
        else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible}) {
                Icon(
                    painter =
                    if (passwordVisible) painterResource(R.drawable.ic_visibility_foreground)
                    else painterResource(R.drawable.ic_visibilityoff_foreground),
                    contentDescription = stringResource(R.string.toggle_password_visibility),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
fun LanguageChangeScreen(
    onLanguageSelected: (Language) -> Unit,
    currentLanguage: Language?
) {
    Dialog(onDismissRequest = { /* Dismiss the dialog */ }) {
        Column(modifier = Modifier
            .background(Color(255, 255, 255))
        )
        {
            Text(text = stringResource(id = R.string.change_language))
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(Language.entries.size) { index ->
                    val language = Language.entries[index]
                    val isSelected = language == currentLanguage
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(16.dp)
                    ) {
                        Text(text = language.name) // 語言名稱
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            imageVector = if (isSelected)
                                Icons.Filled.Check // 選中圖標
                            else
                                Icons.Outlined.Clear, // 未選中圖標
                            contentDescription = stringResource(R.string.language_selection) // 語言選擇
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NameChangeScreen(
    viewModel: Setting,
    name:String,
    onClose: () -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    Dialog(onDismissRequest = { onClose() }) { // 添加 onClose 参数
        Column(modifier = Modifier
            .background(Color(255, 255, 255))
        )
        {
            Text(text = stringResource(id = R.string.change_user_name),
                fontSize = 20.sp)
            EditInputField(label = R.string.input_new_username, // 修改为用户名的标签
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = newName, onValueChanged = {newName=it})
            Button(onClick = { viewModel.changeName(newName) }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    }
}
@Composable
fun PasswordChangeScreen(
    viewModel: Setting,
    old:String,
    new:String,
    onClose: () -> Unit
) {
    var oldp by remember { mutableStateOf(old) }
    var newp by remember { mutableStateOf(new) }
    Dialog(onDismissRequest = { onClose() }) {
        Column(modifier = Modifier
            .background(Color(255, 255, 255))
        )
        {
            Text(text = stringResource(id = R.string.change_password),
                fontSize = 20.sp)
            EditInputField(label = R.string.input_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next),
                value = old, onValueChanged = {oldp=it})
            EditInputField(label = R.string.input_new_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = new, onValueChanged = {newp=it})
            Button(onClick = { viewModel.changePassword(old,new) }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    }
}

@Composable
fun EmailChangeScreen(
    viewModel: Setting,
    email:String,
    onClose: () -> Unit
) {
    var newe by remember { mutableStateOf(email) }
    Dialog(onDismissRequest = {onClose() }) {
        Column(modifier = Modifier
            .background(Color(255, 255, 255))
        )
        {
            Text(text = stringResource(id = R.string.change_user_name),
                fontSize = 20.sp)
            EditInputField(label = R.string.change_email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done),
                value = email, onValueChanged = {newe=it})
            Button(onClick = {viewModel.changeName(email)}) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    }
}

@Composable
fun SingleSelectCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!isChecked) }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null // Disable direct checkbox interaction
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = text, fontSize = 20.sp)
        Spacer(modifier = Modifier.padding(12.dp))
    }
}


@Composable
fun Navigationbar(
    current:Int,
    navController: NavHostController,
){
    var currentSelect by remember {
        mutableIntStateOf(current)
    }
    val menuData = listOf(
        BottomNavItem(
            stringResource(R.string.Route_RequestPage),
            stringResource(R.string.request),
            Icons.Filled.AddCircle)
        ,
        BottomNavItem(
            stringResource(R.string.Route_HomePage),
            stringResource(R.string.home_page),
            Icons.Filled.Home)
        ,
        BottomNavItem(
            stringResource( R.string.Route_SettingPage),
            stringResource(R.string.setting_page),
            Icons.Filled.Settings)
    )
    NavigationBar(containerColor = Color(8, 79, 209),
        contentColor = Color(255, 255, 255),
        tonalElevation = 12.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        menuData.forEachIndexed { index, bottomItemData ->
            NavigationBarItem(
                colors= NavigationBarItemDefaults.colors(Color(0,0,0)),
                selected = currentDestination?.hierarchy?.any {
                    it.route == bottomItemData.route
                } == true,
                icon = {
                    Icon(
                        imageVector = bottomItemData.icon,
                        contentDescription = "點選按鈕",
                        tint = Color(255,255,255)
                    )
                },
                onClick = {
                    currentSelect = index // 更新当前选中索引
                    navController.navigate(bottomItemData.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Navigationbar2(
    current:Int,
    navController: NavHostController,
){
    var currentSelect by remember {
        mutableIntStateOf(current)
    }
    val menuData = listOf(
        BottomNavItem(
            "HelpListPage",
            stringResource(R.string.list),
            Icons.Filled.Menu)
        ,
        BottomNavItem(
            stringResource( R.string.Route_SettingPage),
            stringResource(R.string.setting_page),
            Icons.Filled.Settings)
    )
    NavigationBar(containerColor = Color(8, 79, 209),
        contentColor = Color(255, 255, 255),
        tonalElevation = 12.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        menuData.forEachIndexed { index, bottomItemData ->
            NavigationBarItem(
                colors= NavigationBarItemDefaults.colors(Color(0,0,0)),
                selected = currentDestination?.hierarchy?.any {
                    it.route == bottomItemData.route
                } == true,
                icon = {
                    Icon(
                        imageVector = bottomItemData.icon,
                        contentDescription = "點選按鈕",
                        tint = Color(255,255,255)
                    )
                },
                onClick = {
                    currentSelect = index // 更新当前选中索引
                    navController.navigate(bottomItemData.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


