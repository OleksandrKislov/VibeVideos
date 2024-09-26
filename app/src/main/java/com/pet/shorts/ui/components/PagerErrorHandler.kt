package com.pet.shorts.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import com.pet.shorts.R
import com.pet.shorts.domain.errors.NetworkError

@Composable
fun PagerErrorHandler(
    error: LoadState.Error,
    retryLoading: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (error.error) {
            is NetworkError.NoInternetConnection -> {
                ErrorDialog(
                    title = stringResource(R.string.internet_connection_problem),
                    text = stringResource(R.string.please_check_your_internet_connection),
                    icon = Icons.Default.Warning,
                    confirmButtonText = stringResource(R.string.retry),
                    onConfirmClick = retryLoading
                )
            }

            is NetworkError.HttpTooManyRequests -> {
                ErrorDialog(
                    title = stringResource(R.string.too_many_requests),
                    text = stringResource(R.string.please_try_again_later),
                    icon = Icons.Default.Warning,
                    confirmButtonText = stringResource(R.string.retry),
                    onConfirmClick = retryLoading
                )
            }

            else -> {
                ErrorDialog(
                    title = stringResource(R.string.oops),
                    text = stringResource(R.string.something_went_wrong),
                    icon = Icons.Default.Warning,
                    confirmButtonText = stringResource(R.string.retry),
                    onConfirmClick = retryLoading
                )
            }
        }
    }
}