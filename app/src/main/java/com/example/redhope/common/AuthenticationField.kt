package com.example.redhope.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.redhope.R


@Composable
fun AuthenticationTextField(
    value: String,
    onValueChange:(String) -> Unit,
    label:String,
    @DrawableRes leadingIcon:Int?=null,
    isPassword: Boolean =false,
    error: String?,
    enabled: Boolean=true
){

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange ,
        label = { Text(label, color = if (error != null) MaterialTheme.colorScheme.secondary else Color.Gray) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (error != null) MaterialTheme.colorScheme.secondary else Color.Gray,
            unfocusedBorderColor = if (error != null) MaterialTheme.colorScheme.secondary else Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        enabled= enabled,
        leadingIcon = {
            if (leadingIcon != null){
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = "$label icon",
                    modifier = Modifier.size(24.dp),
                    tint =  if(error != null) MaterialTheme.colorScheme.secondary else Color.Gray
                )
            }

        },
        visualTransformation = if(isPassword && passwordVisible){
            PasswordVisualTransformation() }else VisualTransformation.None,


            trailingIcon = {
                if(isPassword){
                    IconButton({passwordVisible = !passwordVisible}){
                        Icon(
                            painter = painterResource(id = if(passwordVisible) R.drawable.eye else R.drawable.hidden),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            modifier = Modifier.size(24.dp),
                            tint = if (error != null) MaterialTheme.colorScheme.secondary else Color.Gray
                        )
                    }
                }
            }

    )
    if (error!=null){
        Box(modifier = Modifier.fillMaxWidth()){
            Text(text = error, color = MaterialTheme.colorScheme.secondary)
        }

    }
}

