package com.example.project

import Data.BottomNavItem
import ViewModels.CameraViewModel
import ViewModels.Setting
import ViewModels.TTS
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


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
        label = { Text(stringResource(label), color = Color.Black)},
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
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
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
            .border(
                width = 8.dp, color = Color(2, 115, 115),
                shape = RoundedCornerShape(4.dp)
            ),
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
                .fillMaxWidth()
                .padding(8.dp),
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
fun AddScreen(
    context:Context,
    tts: TTS,
    cameraViewModel: CameraViewModel,
    onClose: () -> Unit
){
    var username by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // 保存选择的照片
    var showPhotoDialog by remember { mutableStateOf(false) } // 控制对话框显示
    var cameraState by remember { mutableStateOf(false) }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri // 处理相簿照片选择结果
    }

    // 创建拍照 intent
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // 拍照成功，使用拍照前创建的 Uri
            selectedImageUri = photoUri
            cameraState=true
        } else {
            // 如果失败，清除 Uri
            photoUri = null
            cameraState=true
            Log.e("camera","error")
        }
    }

    LaunchedEffect(cameraState) {
        if (cameraState){
            cameraViewModel.initialize()
        }
    }

    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(242, 231, 220), shape = RoundedCornerShape(20.dp))
                .border(
                    width = 8.dp, color = Color(2, 115, 115),
                    shape = RoundedCornerShape(4.dp)
                ),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = "新增人物",
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.Black)
            Spacer(modifier = Modifier.padding(8.dp))
            EditInputField2(
                label = R.string.name_1, // 修改为用户名的标签
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        Color(213, 204, 204, 255),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .size(width = 268.dp, height = 268.dp)
                    .clickable {
                        showPhotoDialog = true // 点击后弹出选择对话框
                    }
            ) {
                selectedImageUri?.let { uri ->
                    // 如果有选择的图片，则显示图片
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp)
                    )
                } ?: Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "启动相机或选择图片",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                        text = stringResource(id = R.string.close),
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        if (username != ""&&selectedImageUri!=null) {
                            cameraViewModel.uploadPhoto(selectedImageUri,username)
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

    if (showPhotoDialog) {
        AlertDialog(
            modifier = Modifier.border(
                width = 8.dp, color = Color(2, 115, 115),
                shape = RoundedCornerShape(4.dp)),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(242, 231, 220),
            onDismissRequest = { showPhotoDialog = false },
            title = { Text(text = "選擇圖片來源",
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = Color.Black)
                    },
            buttons = {
                Column(modifier = Modifier.width(280.dp),
                    horizontalAlignment=Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center)
                {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        colors = ButtonDefaults.buttonColors(Color(3, 140, 127)),
                        modifier = Modifier.size(200.dp, 40.dp),
                        onClick = {
                            photoPickerLauncher.launch("image/*") // 从相簿选择
                            showPhotoDialog = false
                        }
                    )
                    {
                        Text(
                            text = "從相簿選擇",
                            fontSize = 18.sp, color = Color.White
                        )
                    }
                    Spacer(Modifier.padding(8.dp))
                    Button(shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        colors = ButtonDefaults.buttonColors(Color(3, 140, 127)),
                        modifier = Modifier.size(200.dp, 40.dp),
                        onClick = {
                            cameraViewModel.closeCamera()
                            photoUri = cameraViewModel.createImageUri(context) // 创建用于保存拍照的 Uri
                            try {
                                takePictureLauncher.launch(photoUri!!)
                            } catch (e: Exception) {
                                Log.e("CameraError", "Failed to take picture: ${e.message}")
                            }// 启动相机拍照
                            showPhotoDialog = false
                        }
                    )
                    {
                        Text(
                            text ="拍照",
                            fontSize = 18.sp, color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        )
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

