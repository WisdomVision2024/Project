package com.example.project

import Data.BottomNavItem
import ViewModels.Setting
import ViewModels.TTS
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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontFamily
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
        textStyle = TextStyle(fontSize = 20.sp),
        onValueChange = onValueChanged,
        singleLine = true,
        keyboardOptions=keyboardOptions,
        label = { Text(stringResource(label), color = Color.Black) },
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
        textStyle = TextStyle(fontSize = 20.sp),
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
fun NameChangeScreen(
    viewModel: Setting,
    account:String?,
    onClose: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var oldp by remember { mutableStateOf("") }
    var newp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Dialog(onDismissRequest = { }) { // 添加 onClose 参数
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220), shape = RoundedCornerShape(20.dp))
            .border(width = 8.dp, color = Color(2, 115, 115),
                shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = stringResource(R.string.change_user_data),
                fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(
                label = R.string.input_new_username, // 修改为用户名的标签
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = username,
                onValueChanged = {username=it}
            , modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(170, 219, 182, 255))
            )
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(label = R.string.input_new_email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done),
                value = email, onValueChanged = {email=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(170, 219, 182, 255)))
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(label = R.string.input_new_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = newp, onValueChanged = {newp=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(170, 219, 182, 255)))
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(label = R.string.input_password_required,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done),
                value = oldp, onValueChanged = {oldp=it},
                modifier = Modifier
                    .width(268.dp)
                    .height(60.dp)
                    .background(Color(170, 219, 182, 255)))
            Spacer(modifier = Modifier.padding(20.dp))
            Row(modifier = Modifier
                .fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
                ) {
                Button(
                    onClick = { onClose() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(2, 115, 115)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        if (account != null) {
                            viewModel.changeName(account, username, email, newp, oldp)
                        }
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessageScreen(
    errorMessage: String,
    tts: TTS,
    onClose: () -> Unit
) {
    DisposableEffect(Unit) {
        tts.speak(errorMessage)
        onDispose {
            tts.stop()
        }
    }
    Dialog(onDismissRequest = { }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220), shape = RoundedCornerShape(20.dp))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(text = errorMessage, fontSize = 24.sp, color = Color.Black)
            Button(onClick = {onClose()},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp))
            {
                Text(text = stringResource(id = R.string.confirm)
                    , color = Color.White, fontSize = 16.sp,
                    fontFamily = FontFamily.Serif)
            }
        }
    }
}

@Composable
fun FinishScreen(
    tts: TTS,
    onClose: () -> Unit
) {
    val text= stringResource(R.string.success)
    DisposableEffect (Unit) {
        tts.speak(text)
        onDispose {
            tts.stop()
        }
    }
    Dialog(onDismissRequest = { }) {
        Column(modifier = Modifier
            .width(320.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(242, 231, 220), shape = RoundedCornerShape(20.dp))
            .border(width = 8.dp, color = Color(2, 115, 115), shape = RoundedCornerShape(4.dp)),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(text = text, fontSize = 24.sp, color = Color.Black)
            Spacer(Modifier.padding(12.dp))
            Button(onClick = {onClose()},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier.height(40.dp))
            {
                Text(text = stringResource(id = R.string.confirm),
                    color = Color.White, fontSize = 16.sp,
                    fontFamily = FontFamily.Serif)
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

