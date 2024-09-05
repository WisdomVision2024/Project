package com.example.project

import Data.BottomNavItem
import ViewModels.Setting
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
        shape = RoundedCornerShape(12.dp),
        keyboardOptions=keyboardOptions,
        label = { Text(stringResource(label)) },
        modifier=modifier
    )
}

@Composable
fun EditInputField2(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
)
{
    TextField(
        value = value,
        textStyle = TextStyle(fontSize = 12.sp),
        onValueChange = onValueChanged,
        singleLine = true,
        keyboardOptions=keyboardOptions,
        label = { Text(stringResource(label)) },
        modifier= modifier
            .height(64.dp)
            .width(320.dp)
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
    TextField(
        value = value,
        textStyle = TextStyle(fontSize = 12.sp),
        onValueChange = onValueChanged,
        singleLine = true,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        modifier= modifier
            .height(64.dp)
            .width(320.dp),
        visualTransformation =
        if (!passwordVisible) PasswordVisualTransformation()
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
fun LanguageChangeScreen(onClose: () -> Unit) {
    val localeOptions = mapOf(
        R.string.english to "en-rUS",
        R.string.french to "fr",
        R.string.chinese to "zh-rTW",
        R.string.japanese to "ja",
        R.string.korean to "ko-rKR"
    ).mapKeys { stringResource(it.key) }
    Dialog(onDismissRequest = {onClose()}) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .height(400.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(255, 255, 255))
                .border(
                    width = 8.dp,
                    color = Color(2, 115, 115),
                    shape = RoundedCornerShape(4.dp)
                ),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(id = R.string.change_language),
                fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                localeOptions.keys.forEach { selectionLocale ->
                    Box (modifier = Modifier
                        .clickable {
                            // set app locale given the user's selected locale
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(
                                    localeOptions[selectionLocale]
                                )
                            )// Optionally, you can navigate back or perform other actions
                        }
                        .padding(20.dp)
                    )
                    {
                        Text(
                            text = selectionLocale,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable {
                                    // set app locale given the user's selected locale
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags(
                                            localeOptions[selectionLocale]
                                        )
                                    )// Optionally, you can navigate back or perform other actions
                            }
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
    account:String?,
    onClose: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onClose() }) { // 添加 onClose 参数
        Column(modifier = Modifier
            .width(320.dp)
            .height(400.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.padding(20.dp))
            Text(text = stringResource(id = R.string.change_user_name),
                fontSize = 32.sp)
            Spacer(modifier = Modifier.padding(40.dp))
            EditInputField2(
                label = R.string.input_new_username, // 修改为用户名的标签
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = username,
                onValueChanged = {username=it}
            , modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(169, 217, 108))
            )
            Spacer(modifier = Modifier.padding(40.dp))
            Button(onClick = { viewModel.changeName(account,username) },
                colors = ButtonDefaults.buttonColors(containerColor=Color.Red),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = stringResource(id = R.string.confirm),
                    color = Color.White
                )
            }
        }
    }
}
@Composable
fun PasswordChangeScreen(
    viewModel: Setting,
    account:String?,
    onClose: () -> Unit
) {
    var oldp by remember { mutableStateOf("") }
    var newp by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { onClose() }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(400.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.padding(20.dp))
            Text(text = stringResource(id = R.string.change_password),
                fontSize = 20.sp)
            Spacer(modifier = Modifier.padding(20.dp))
            EditInputField2(label = R.string.input_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next),
                value = oldp, onValueChanged = {oldp=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(169, 217, 108)))
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(label = R.string.input_new_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = newp, onValueChanged = {newp=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(169, 217, 108)))
            Spacer(modifier = Modifier.padding(20.dp))
            Button(onClick = { viewModel.changePassword(account,oldp,newp) },
                colors = ButtonDefaults.buttonColors(Color.Red),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
            }
        }
    }
}

@Composable
fun EmailChangeScreen(
    viewModel: Setting,
    account:String?,
    onClose: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    Dialog(onDismissRequest = {onClose() }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(400.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(
                width = 8.dp, color = Color(2, 115, 115),
                shape = RoundedCornerShape(4.dp)
            ),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.padding(20.dp))
            Text(text = stringResource(id = R.string.change_email),
                fontSize = 20.sp)
            Spacer(modifier = Modifier.padding(40.dp))
            EditInputField2(label = R.string.change_email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done),
                value = email, onValueChanged = {email=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(169, 217, 108)))
            Spacer(modifier = Modifier.padding(40.dp))
            Button(onClick = {viewModel.changeEmail(account,email)},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
            }
        }
    }
}

@Composable
fun ErrorMessageScreen(
    errorMessage: String,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = {onClose() }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(text = errorMessage, fontSize = 20.sp, color = Color.White)
            Button(onClick = {onClose()},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
            }
        }
    }
}

@Composable
fun HelpScreen(
    onClose: () -> Unit,
) {
    Dialog(onDismissRequest = {onClose() }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Row {

            }
            Button(onClick = {onClose()},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm), color = Color.White)
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
            colors = androidx.compose.material3.CheckboxDefaults.colors(Color(2,115,115)),
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
    navController: NavController,
){
    var currentSelect by remember {
        mutableIntStateOf(current)
    }
    val menuData = listOf(
        BottomNavItem(
            stringResource(R.string.Route_HomePage),
            stringResource(R.string.home_page),
            Icons.Filled.Home) ,
        BottomNavItem(
            stringResource(R.string.Route_RequestPage),
            stringResource(R.string.request),
            Icons.Filled.Person)
    )
    NavigationBar(containerColor = Color(3, 140, 127),
        contentColor = Color(0, 0, 0),
        tonalElevation = 12.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        menuData.forEachIndexed { index, bottomItemData ->
            NavigationBarItem(
                colors= NavigationBarItemDefaults.colors(Color(255,255,255)),
                selected = currentDestination?.hierarchy?.any {
                    it.route == bottomItemData.route
                } == true,
                icon = {
                    Icon(
                        imageVector = bottomItemData.icon,
                        contentDescription = "click",
                        tint = Color(0,0,0)
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
    navController: NavController,
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
    NavigationBar(containerColor = Color(3, 140, 127),
        contentColor = Color(0, 0, 0),
        tonalElevation = 12.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        menuData.forEachIndexed { index, bottomItemData ->
            NavigationBarItem(
                colors= NavigationBarItemDefaults.colors(Color(255,255,255)),
                selected = currentDestination?.hierarchy?.any {
                    it.route == bottomItemData.route
                } == true,
                icon = {
                    Icon(
                        imageVector = bottomItemData.icon,
                        contentDescription = "點選按鈕",
                        tint = Color(0,0,0)
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
