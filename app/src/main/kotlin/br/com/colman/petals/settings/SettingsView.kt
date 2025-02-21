@file:OptIn(ExperimentalMaterialApi::class)
@file:Suppress("TooManyFunctions")

package br.com.colman.petals.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import br.com.colman.petals.R.string.app_pin
import br.com.colman.petals.R.string.currency_icon
import br.com.colman.petals.R.string.date_format_label
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_bar_on_home_page
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_on_hit_timer_page
import br.com.colman.petals.R.string.hit_timer_milliseconds_enabled
import br.com.colman.petals.R.string.milliseconds_enabled
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.password_description
import br.com.colman.petals.R.string.repository_link_description
import br.com.colman.petals.R.string.repository_link_title
import br.com.colman.petals.R.string.share_app
import br.com.colman.petals.R.string.share_app_message
import br.com.colman.petals.R.string.share_app_title
import br.com.colman.petals.R.string.time_format_label
import br.com.colman.petals.R.string.what_date_format_should_be_used
import br.com.colman.petals.R.string.what_icon_should_be_used_for_currency
import br.com.colman.petals.R.string.what_time_format_should_be_used
import compose.icons.TablerIcons
import compose.icons.tablericons.BrandGithub
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Cash
import compose.icons.tablericons.CircleOff
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Lock
import compose.icons.tablericons.ToggleLeft

@Composable
fun SettingsView(settingsRepository: SettingsRepository) {
  val currentCurrency by settingsRepository.currencyIcon.collectAsState("$")
  val setCurrency = settingsRepository::setCurrencyIcon
  val setDateFormat = settingsRepository::setDateFormat
  val dateFormatList = settingsRepository.dateFormatList
  val currentDateFormat by settingsRepository.dateFormat.collectAsState(dateFormatList[0])
  val setTimeFormat = settingsRepository::setTimeFormat
  val timeFormatList = settingsRepository.timeFormatList
  val currentTimeFormat by settingsRepository.timeFormat.collectAsState(timeFormatList[0])
  val setPin = settingsRepository::setPin
  val setMillisecondsEnabled = settingsRepository::setMillisecondsEnabled
  val millisecondsEnabledList = settingsRepository.millisecondsEnabledList
  val currentMillisecondsEnabled by settingsRepository.millisecondsEnabled.collectAsState(millisecondsEnabledList[0])
  val setHitTimerMillisecondsEnabled = settingsRepository::setHitTimerMillisecondsEnabled
  val hitTimerMillisecondsEnabledList = settingsRepository.hitTimerMillisecondsEnabledList
  val currentHitTimerMillisecondsEnabled by settingsRepository.hitTimerMillisecondsEnabled.collectAsState(
    hitTimerMillisecondsEnabledList[0]
  )

  Column {
    CurrencyListItem(currentCurrency, setCurrency)
    PinListItem(setPin)
    RepositoryListItem()
    DateListItem(currentDateFormat, dateFormatList, setDateFormat)
    TimeListItem(currentTimeFormat, timeFormatList, setTimeFormat)
    MillisecondsEnabledListItem(currentMillisecondsEnabled, millisecondsEnabledList, setMillisecondsEnabled)
    HitTimerMillisecondsEnabledListItem(
      currentHitTimerMillisecondsEnabled,
      hitTimerMillisecondsEnabledList,
      setHitTimerMillisecondsEnabled
    )
    ShareApp()
  }
}

@Preview
@Composable
private fun RepositoryListItem() {
  val uriHandler = LocalUriHandler.current
  val openUrl = { uriHandler.openUri("https://github.com/LeoColman/Petals") }

  ListItem(
    modifier = Modifier.clickable { openUrl() },
    icon = { Icon(TablerIcons.BrandGithub, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(repository_link_description)) }
  ) {
    Text(stringResource(repository_link_title))
  }
}

@Preview
@Composable
private fun CurrencyListItem(
  currency: String = "",
  setCurrency: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Cash, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_icon_should_be_used_for_currency)) }
  ) {
    Text(stringResource(currency_icon))
  }

  if (shouldShowDialog) {
    CurrencyDialog(currency, setCurrency) { shouldShowDialog = false }
  }
}

@Preview
@Composable
private fun PinListItem(
  setPin: (String?) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Lock, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(password_description)) }
  ) {
    Text(stringResource(app_pin))
  }

  if (shouldShowDialog) {
    PinDialog(setPin) { shouldShowDialog = false }
  }
}

@Preview
@Composable
fun DateListItem(
  dateFormat: String = "",
  dateFormatList: List<String> = listOf(),
  setDateFormat: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Calendar, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_date_format_should_be_used)) }
  ) {
    Text(stringResource(date_format_label))
  }

  if (shouldShowDialog) {
    DateDialog(dateFormat, dateFormatList, setDateFormat) { shouldShowDialog = false }
  }
}

@Preview
@Composable
fun TimeListItem(
  timeFormat: String = "",
  timeFormatList: List<String> = listOf(),
  setTimeFormat: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Clock, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_time_format_should_be_used)) }
  ) {
    Text(stringResource(time_format_label))
  }

  if (shouldShowDialog) {
    TimeDialog(timeFormat, timeFormatList, setTimeFormat) { shouldShowDialog = false }
  }
}

@Preview
@Composable
fun MillisecondsEnabledListItem(
  millisEnabled: String = "",
  millisOptions: List<String> = listOf(),
  setMillisEnabled: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.CircleOff, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(enable_or_disable_milliseconds_bar_on_home_page)) }
  ) {
    Text(stringResource(milliseconds_enabled))
  }

  if (shouldShowDialog) {
    MillisecondsEnabledDialog(millisEnabled, millisOptions, setMillisEnabled) {
      shouldShowDialog = false
    }
  }
}

@Preview
@Composable
fun HitTimerMillisecondsEnabledListItem(
  hitTimerMillisecondsEnabled: String = "",
  hitTimerMillisecondsEnabledList: List<String> = listOf(),
  setHitTimerMillisecondsEnabled: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.ToggleLeft, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(enable_or_disable_milliseconds_on_hit_timer_page)) }
  ) {
    Text(stringResource(hit_timer_milliseconds_enabled))
  }

  if (shouldShowDialog) {
    HitTimerMillisecondsEnabledDialog(
      hitTimerMillisecondsEnabled,
      hitTimerMillisecondsEnabledList,
      setHitTimerMillisecondsEnabled
    ) { shouldShowDialog = false }
  }
}

@Preview
@Composable
fun ShareApp(
  shareIcon: ImageVector = Icons.Default.Share,
  context: Context = LocalContext.current
) {
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(
      Intent.EXTRA_TEXT,
      stringResource(share_app_message)
    )
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, null)
  ListItem(
    modifier = Modifier.clickable {
      ContextCompat.startActivity(context, shareIntent, null)
    },
    icon = { Icon(shareIcon, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(share_app)) }
  ) {
    Text(stringResource(share_app_title))
  }
}

@Preview
@Composable
private fun TimeDialog(
  timeFormat: String = "",
  timeFormatList: List<String> = listOf(),
  setTimeFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var timeFormat by remember { mutableStateOf(timeFormat) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }
      ) {
        TextField(
          value = timeFormat,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(time_format_label)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          timeFormatList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              timeFormat = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setTimeFormat(timeFormat); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun DateDialog(
  dateFormat: String = "",
  dateFormatList: List<String> = listOf(),
  setDateFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var dateFormat by remember { mutableStateOf(dateFormat) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }
      ) {
        TextField(
          value = dateFormat,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(date_format_label)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          dateFormatList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              dateFormat = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setDateFormat(dateFormat); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun CurrencyDialog(
  currency: String = "$",
  setCurrency: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var currency by remember { mutableStateOf(currency) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      OutlinedTextField(
        currency,
        { currency = it },
        label = { Text(stringResource(currency_icon)) }
      )
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setCurrency(currency); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun PinDialog(
  setPin: (String?) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var pin by remember { mutableStateOf(null as String?) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      OutlinedTextField(
        pin.orEmpty(),
        { pin = it.ifBlank { null } },
        label = { Text(stringResource(app_pin)) },
        visualTransformation = PasswordVisualTransformation()
      )
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setPin(pin); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun MillisecondsEnabledDialog(
  millisecondsEnabled: String = "",
  millisecondsEnabledList: List<String> = listOf(),
  setMillisecondsEnabled: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var millisecondsEnabled by remember { mutableStateOf(millisecondsEnabled) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }
      ) {
        TextField(
          value = millisecondsEnabled,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(milliseconds_enabled)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          millisecondsEnabledList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              millisecondsEnabled = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setMillisecondsEnabled(millisecondsEnabled); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun HitTimerMillisecondsEnabledDialog(
  hitTimerMillisecondsEnabled: String = "",
  hitTimerMillisecondsEnabledList: List<String> = listOf(),
  setHitTimerMillisecondsEnabled: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var hitTimerMillisecondsEnabled by remember { mutableStateOf(hitTimerMillisecondsEnabled) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }
      ) {
        TextField(
          value = hitTimerMillisecondsEnabled,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(hit_timer_milliseconds_enabled)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          hitTimerMillisecondsEnabledList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              hitTimerMillisecondsEnabled = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setHitTimerMillisecondsEnabled(hitTimerMillisecondsEnabled); onDismiss() }
      )
    }
  )
}
