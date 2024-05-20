package com.example.project

import Language.Language
import ViewModels.SignupUiState
import ViewModels.Signup
import ViewModels.LoginUiState
import ViewModels.Login
import Data.BottomNavItem
import Data.LoginState
import DataStore.LanguageSettingsStore
import DataStore.LoginDataStore
import ViewModels.Identified
import ViewModels.Setting
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import assets.ApiService
import com.example.project.ui.theme.ProjectTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val languageSettingsStore = LanguageSettingsStore()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        setContent {
            val loginDataStore = LoginDataStore(LocalContext.current.applicationContext)
            val dataStore = loginDataStore.createLoginDataStore(LocalContext.current)
            val loginStateFlow = loginDataStore.loadLoginState(dataStore)
            val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
            val apiService = retrofit.create(ApiService::class.java)
            val navController = rememberNavController()
            ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        loginState = loginState,
                        navController = navController,
                        apiService = apiService,
                        loginDataStore = loginDataStore ,
                        languageSettingsStore=languageSettingsStore)
                }
            }
        }
    }

    private val viewModel: Identified by viewModels()

    private val multiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val grantedPermissions = permissions.filterValues { it }
            val deniedPermissions = permissions.filterValues { !it }

            // 可以抽出一个函数来处理每个权限的日志输出
            logPermissions(grantedPermissions, deniedPermissions)

            // 将授权结果传递给 ViewModel 中更明确的函数，比如 handlePermissions(grantedPermissions)
            viewModel.setupPermissionLauncher(grantedPermissions,applicationContext)
        }

    private fun logPermissions(granted: Map<String, Boolean>, denied: Map<String, Boolean>) {
        granted.forEach { (permission, isGranted) ->
            Log.d(permission, "$permission is granted: $isGranted")
        }
        denied.forEach { (permission, isGranted) ->
            Log.d(permission, "$permission is granted: $isGranted")
        }
    }

    companion object {
        lateinit var instance: MainActivity
            private set
    }
}
private val retrofit = Retrofit.Builder()
    .baseUrl("https://your-server.com/") // 這裡放你的伺服器 URL
    .addConverterFactory(GsonConverterFactory.create())
    .build()

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    val loginDataStore = LoginDataStore(LocalContext.current.applicationContext)
    val dataStore = loginDataStore.createLoginDataStore(LocalContext.current)
    val loginStateFlow = loginDataStore.loadLoginState(dataStore)
    val loginState by loginStateFlow.collectAsState(initial = LoginState(false, null))
    val apiService = retrofit.create(ApiService::class.java)
    val navController = rememberNavController()
    Navigation(
        loginState = loginState,
        navController = navController,
        apiService = apiService,
        loginDataStore =loginDataStore,
        LanguageSettingsStore()
    )
}

@Preview(showBackground = true)
@Composable
fun SignupPagePreview() {
    val apiService = retrofit.create(ApiService::class.java)
    val navController = rememberNavController()
    SignupPage(viewModel = Signup(apiService),navController)
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val navController = rememberNavController()
    HomePage(viewModel = Identified(),
        navController = navController, onRecordingStarted = { println("Recording started")})
}
@Preview(showBackground = true)
@Composable
fun RequestPagePreview() {
    val navController = rememberNavController()
    RequestPage(navController)
}

@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    val apiService = retrofit.create(ApiService::class.java)
    val navController = rememberNavController()
    val languageSettingsStore = LanguageSettingsStore()
    SettingPage(viewModel = Setting(apiService),languageSettingsStore,navController)
}

@Composable
fun Navigation(loginState: LoginState,
               navController: NavHostController,
               apiService: ApiService,
               loginDataStore: LoginDataStore,
               languageSettingsStore: LanguageSettingsStore) {
    val currentLanguage by remember { mutableStateOf(languageSettingsStore.currentLanguage) }
    NavHost(navController = navController, startDestination = "LoginPage")
    {
        if (loginState.isLoggedIn) {
            if (loginState.currentUser?.isVisuallyImpaired == true) {
                composable(route = "HomePage") {HomePage(viewModel = Identified(),
                    navController = navController, onRecordingStarted = { println("Recording started")}) }
            } else {
                composable(route = "HelpListPage") { HelpListPage(navController = navController) }
            }
        } else {
            composable(route = "LoginPage") {
                LoginPage(
                    viewModel = Login(
                        apiService,
                        LocalContext.current.applicationContext,
                        loginDataStore
                    ),
                    navController = navController
                )
            }
        }
        composable(route = "LoginPage") { LoginPage(viewModel = Login(apiService,
            LocalContext.current.applicationContext,
            loginDataStore),navController = navController) }
        composable(route = "SignupPage") { SignupPage(viewModel = Signup(apiService),navController = navController) }
        composable(route = "RequestPage") { RequestPage(navController = navController) }
        composable(route = "HomePage") { HomePage(viewModel = Identified(),
            navController = navController, onRecordingStarted = { println("Recording started")}) }
        composable(route = "SettingPage") { SettingPage(viewModel = Setting(apiService),
            languageSettingsStore,navController = navController) }
        composable(route = "HelpListPage") { HelpListPage(navController = navController) }
    }
}

@Composable
fun EditInputField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier)
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
    modifier: Modifier = Modifier)
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
        Column(modifier = Modifier.background(Color(255,255,255))) {
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
fun LoginPage(viewModel: Login,
              navController: NavController
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.loginState.collectAsState().value
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                stringResource(R.string.name),
                fontSize = 100.sp,
                lineHeight = 70.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(30.dp))
            EditInputField(
                value = account,
                onValueChanged = { account = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                label = R.string.account, modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(16.dp))
            PasswordInputField(
                value = password,
                onValueChanged = { password = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                label = R.string.password,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("SignupPage") },
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(96.dp)
                )
                {
                    Text(text = stringResource(R.string.sign_up))
                }
                Button(
                    onClick = { viewModel.login(account, password) },
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(96.dp)
                ) {
                    Text(stringResource(R.string.log_in))
                }
            }
            LaunchedEffect(state) {
                when (state) {
                    is LoginUiState.Success -> {
                        navController.navigate("HomePage") {
                            popUpTo("LoginPage") {
                                inclusive = true
                            }
                        }
                    }
                    is LoginUiState.Error -> {
                        val message = state.message
                        scaffoldState.snackbarHostState.showSnackbar(message)
                    }
                    else -> {
                        Unit
                    }
                }
            }
        }
    }
}

@Composable
fun SignupPage(viewModel: Signup,
               navController: NavHostController
) {
    var account by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatpassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isVisuallyImpaired by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val state = viewModel.registerState.collectAsState().value
    val isCorrect: Boolean = repeatpassword == password
    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.input_phone_num),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = account,
                    onValueChanged = { account = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                    ),
                    label = R.string.account,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_user_name),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = username,
                    onValueChanged = { username = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    ),
                    label = R.string.username,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_password),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                PasswordInputField(
                    value = password,
                    onValueChanged = { password = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    ),
                    label = R.string.password,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.repeat),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                PasswordInputField(
                    value = repeatpassword,
                    onValueChanged = { repeatpassword = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    label = R.string.repeat,
                    modifier = Modifier
                )
                if (!isCorrect && repeatpassword.isNotEmpty()) {
                    Text(
                        text = "Different from Password!",
                        fontSize = 16.sp,
                        style = TextStyle(Color(255, 0, 0))
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.input_email),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                EditInputField(
                    value = email,
                    onValueChanged = { email = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    label = R.string.phone_number,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Text(
                    text = stringResource(R.string.isVisuallyImpaired),
                    fontSize = 20.sp, textAlign = TextAlign.Left
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SingleSelectCheckbox(
                        isChecked = isVisuallyImpaired,
                        onCheckedChange = {isChecked->
                            isVisuallyImpaired = isChecked
                        },
                        text = stringResource(R.string.yes)
                    )
                    SingleSelectCheckbox(
                        isChecked = !isVisuallyImpaired,
                        onCheckedChange = {isChecked->
                            isVisuallyImpaired = isChecked
                        },
                        text = stringResource(R.string.no)
                    )
                }
                Spacer(modifier = Modifier.padding(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        shape = CircleShape,
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        modifier = Modifier.size(96.dp)
                    )
                    {
                        Text(text = stringResource(R.string.back))
                    }
                    Button(
                        onClick = {
                            viewModel.signup(account, username, password, email, isVisuallyImpaired)
                        },
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        shape = CircleShape,
                        modifier = Modifier.size(96.dp)
                    ) {
                        Text(stringResource(R.string.sign_up))
                    }
                }
                LaunchedEffect(state) {
                    when (state) {
                        is SignupUiState.Success -> {
                            navController.navigate("LoginPage") {
                                popUpTo("SignupPage") {
                                    inclusive = true
                                }
                            }
                        }
                        is SignupUiState.Error -> {
                            val message = state.message
                            scaffoldState.snackbarHostState.showSnackbar(message)
                        }
                        else -> {
                            Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestPage(navController: NavController) {
}

@Composable
fun Navigationbar(
    current:Int,
    navController:NavController,
){
    var currentSelect by remember {
        mutableIntStateOf(current)
    }
    val menuData = listOf(
        BottomNavItem(
            stringResource(R.string.request),
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
    navController:NavController,
){
    var currentSelect by remember {
        mutableIntStateOf(current)
    }
    val menuData = listOf(
        BottomNavItem(
            stringResource(R.string.list),
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

@Composable
fun HomePage(viewModel: Identified,
             onRecordingStarted: () -> Unit,
             navController: NavController) {
    val current=1
    val isvisualimpired=true
    var isRecording by remember { mutableStateOf(false) }
    var recordingFilePath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (isvisualimpired)Navigationbar(current,navController)
        else Navigationbar2(current, navController)
        })
    {
            innerPadding -> println(innerPadding)
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                Button(
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    onClick = {
                        if (isRecording) {
                            viewModel.stopRecording(context, recordingFilePath!!)
                            if (viewModel.isUploadSuccess) {
                                viewModel.deleteRecordingFile(context, recordingFilePath!!)
                            }
                        } else {
                            viewModel.startRecording(context) { filePath ->
                                recordingFilePath = filePath
                                onRecordingStarted()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else Color.Blue
                    ),
                    modifier = Modifier.size(96.dp)
                ) {
                    Text(text = if (isRecording) "Stop" else "Start")
                }

                if (recordingFilePath != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("file name: $recordingFilePath")

                    if (!viewModel.isUploadSuccess) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            Button(onClick = {
                                // Retry uploading
                                viewModel.uploadRecordingFile(context, recordingFilePath!!) { success ->
                                    if (success) {
                                        viewModel.isUploadSuccess = true
                                    }
                                }
                            }) {
                                Text("try again")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(onClick = {
                                // Delete recording
                                viewModel.deleteRecordingFile(context, recordingFilePath!!)
                            }) {
                                Text("delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpListPage(navController: NavController) {
    val current = 0
    val isvisualimpired=true
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (isvisualimpired)Navigationbar(current,navController)
        else Navigationbar2(current, navController)
        })
    { innerPadding ->
        println(innerPadding)
        Box(
            modifier = Modifier
                .background(Color(8, 79, 209))
                .fillMaxWidth()
                .padding(15.dp),
            contentAlignment = Alignment.Center,
        )
        {
        }
    }
}


@Composable
fun SettingPage(viewModel:Setting,languageSettingsStore: LanguageSettingsStore,
                navController: NavController) {
    val context = LocalContext.current
    val dataStore = languageSettingsStore.getDataStore()
    var isSettingContextVisible by remember { mutableStateOf(true) }
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf<Language?>(Language.English) }
    if (dataStore == null) {
        languageSettingsStore.setDataStore(context)
    }
    else {
        val currentLanguageFlow = languageSettingsStore.loadLanguageSettings(dataStore)
            .map { it.language }
        // Get language from settings
        LaunchedEffect(currentLanguageFlow) {
            currentLanguageFlow.onEach { language ->
                currentLanguage = language
            }
        }
        if (isLanguageChangeScreenVisible) {
            currentLanguage?.let {
                LanguageChangeScreen(
                    onLanguageSelected = { selectedLanguage ->
                        viewModel.SaveLanguageSettings(
                            languageSettingsStore, selectedLanguage
                        )
                        isLanguageChangeScreenVisible = false
                    },
                    currentLanguage = currentLanguage
                )
            }
        }
        else{
            SettingContent(
                language = currentLanguage ?: Language.English,
                navController = navController,
                onLanguageChangeRequested = { isLanguageChangeScreenVisible = true })
        }
    }
}
@Composable
fun SettingContent(
    language: Language,
    navController: NavController,
    onLanguageChangeRequested: () -> Unit
    ){
    val isvisualimpired = true
    var isLanguageChangeScreenVisible by remember { mutableStateOf(false) }
    Scaffold (modifier = Modifier.fillMaxSize(),
        bottomBar = { if (isvisualimpired)Navigationbar(2,navController)
        else Navigationbar2(1, navController)
        })
    { innerPadding ->
        println(innerPadding)
        Surface()
        {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    stringResource(id = R.string.user_setting),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { /*TODO*/ })
                {
                    Text(
                        text = stringResource(id = R.string.change_user_name),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { /*TODO*/ })
                {
                    Text(
                        text = stringResource(id = R.string.change_password),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Button(shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { /*TODO*/ })
                {
                    Text(
                        text = stringResource(id = R.string.change_email),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(30.dp))
                Text(
                    stringResource(id = R.string.other_set),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Button(
                    elevation = ButtonDefaults.buttonElevation(4.dp),
                    modifier = Modifier.size(300.dp, 40.dp),
                    onClick = { onLanguageChangeRequested()})
                {
                    Text(
                        text = stringResource(id = R.string.change_language),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))
                Row(
                    modifier = Modifier

                        .padding(12.dp)
                )
                {
                    Button(
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.close_account))
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.help))
                    }
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                )
                {
                    Button(
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.log_out))
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Button(
                        elevation = ButtonDefaults.buttonElevation(4.dp),
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(150.dp, 40.dp)
                    )
                    {
                        Text(text = stringResource(id = R.string.connect))
                    }
                }
            }
        }
    }
}
