package com.hms.player

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun SettingCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Surface1) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = OrangeSoft, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
fun SettingsScreen(vm: AppVM) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var autoPip by remember { mutableStateOf(vm.store.autoPip) }
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize()) {
        TopBar(tr("الإعدادات", "Settings"), onBack = { vm.pop() })
        Column(
            Modifier.fillMaxSize().verticalScroll(scroll).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingCard(tr("الحسابات", "Accounts")) {
                val curName = vm.active?.name ?: "—"
                Text(
                    tr("الحساب الحالي: $curName   •   عدد الحسابات: ${vm.accounts.size}", "Current account: $curName   •   Accounts: ${vm.accounts.size}"),
                    color = TextMain, fontSize = 14.sp
                )
                HmsButton(tr("إدارة الحسابات", "Manage accounts"), icon = Icons.Default.ManageAccounts) { vm.push(Screen.Accounts) }
            }

            SettingCard(tr("اللغة", "Language")) {
                Text(tr("اختار لغة التطبيق.", "Choose the app language."), color = TextMuted, fontSize = 13.sp, lineHeight = 20.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // language names are always written in their own language
                    HmsButton("العربية", icon = Icons.Default.Language, primary = Lang.current == AppLanguage.AR) { vm.changeLanguage(AppLanguage.AR) }
                    HmsButton("English", icon = Icons.Default.Language, primary = Lang.current == AppLanguage.EN) { vm.changeLanguage(AppLanguage.EN) }
                }
            }

            SettingCard(tr("نوع الجهاز", "Device type")) {
                Text(
                    tr("موبايل: التطبيق بالطول، والتشغيل بالعرض.\nتلفزيون: كل حاجة بالعرض.",
                        "Phone: the app is portrait, playback is landscape.\nTV: everything is landscape."),
                    color = TextMuted, fontSize = 13.sp, lineHeight = 20.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HmsButton(tr("موبايل", "Phone"), icon = Icons.Default.Smartphone, primary = vm.deviceMode == DeviceMode.PHONE) { vm.changeMode(DeviceMode.PHONE) }
                    HmsButton(tr("تلفزيون", "TV"), icon = Icons.Default.Tv, primary = vm.deviceMode == DeviceMode.TV) { vm.changeMode(DeviceMode.TV) }
                }
            }

            if (vm.deviceMode == DeviceMode.PHONE && Build.VERSION.SDK_INT >= 26) SettingCard(tr("صورة داخل صورة", "Picture in Picture")) {
                Text(
                    tr("وإنت بتشاهد، لو خرجت من التطبيق (زرار الهوم) الفيديو يكمّل في نافذة صغيرة فوق التطبيقات التانية. وتقدر تفتحها بإيدك من زرار صورة داخل صورة جوه المشغّل.",
                        "While watching, if you leave the app (Home button) the video keeps playing in a small window over other apps. You can also open it manually from the Picture in Picture button inside the player."),
                    color = TextMuted, fontSize = 13.sp, lineHeight = 20.sp
                )
                HmsButton(
                    if (autoPip) tr("الدخول التلقائي: مفعّل ✓", "Auto-enter: On ✓") else tr("الدخول التلقائي: متوقف", "Auto-enter: Off"),
                    icon = Icons.Default.PictureInPictureAlt, primary = autoPip
                ) {
                    autoPip = !autoPip
                    vm.store.autoPip = autoPip
                }
            }

            SettingCard(tr("التحديث", "Update")) {
                Text(tr("الإصدار الحالي: ${Updater.currentVersion(ctx)}", "Current version: ${Updater.currentVersion(ctx)}"), color = TextMain, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HmsButton(tr("التحقق من التحديث", "Check for updates"), icon = Icons.Default.SystemUpdate) {
                        Updater.openStorePage(ctx)
                    }
                }
            }

            Spacer(Modifier.height(120.dp)) // extra room at the bottom so nothing is cut off on TV
        }
    }
}

@Composable
fun BusyDialog(msg: String, onRetry: () -> Unit, onForce: () -> Unit, onCancel: () -> Unit) {
    DialogShell(tr("الحساب مشغول", "Account busy"), onCancel, 0.6f) {
        Text(msg, color = TextMuted, fontSize = 14.sp, lineHeight = 22.sp)
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HmsButton(tr("إعادة المحاولة", "Retry"), icon = Icons.Default.Replay) { onRetry() }
            HmsButton(tr("شغّل رغم كده", "Play anyway"), primary = false, onClick = onForce)
            HmsButton(tr("إلغاء", "Cancel"), primary = false, onClick = onCancel)
        }
    }
}