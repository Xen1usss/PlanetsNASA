package ks.planetsnasa.ui.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import ks.planetsnasa.R

fun share(ctx: Context, title: String, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n$url")
    }
    ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.share_chooser_title)))
}

fun saveWithDownloadManager(ctx: Context, title: String, url: String) {
    val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            "PlanetsNASA/${title.sanitize()}.jpg"
        )
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
    dm.enqueue(request)
}

private fun String.sanitize(): String = replace(Regex("""[^\w\-. ]"""), "_")

