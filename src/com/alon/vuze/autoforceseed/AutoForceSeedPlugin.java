package com.alon.vuze.autoforceseed;

import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginException;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadCompletionListener;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.ui.config.StringParameter;
import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;

public class AutoForceSeedPlugin implements Plugin, DownloadCompletionListener {

  private StringParameter categoriesEdit;
  private TorrentAttribute categoryAttribute;

  public void initialize(PluginInterface pluginInterface) throws PluginException {
    DownloadManager downloadManager = pluginInterface.getDownloadManager();
    final TorrentManager torrentManager = pluginInterface.getTorrentManager();
    categoryAttribute = torrentManager.getAttribute(TorrentAttribute.TA_CATEGORY);

    final DownloadEventNotifier eventNotifier = downloadManager.getGlobalDownloadEventNotifier();
    eventNotifier.addCompletionListener(this);
    createConfigModule(pluginInterface);
  }

  private void createConfigModule(PluginInterface pluginInterface) {
    final BasicPluginConfigModel configModel = pluginInterface.getUIManager()
        .createBasicPluginConfigModel("autoforceseed");
    configModel.addLabelParameter2("config.title");
    categoriesEdit = configModel.addStringParameter2("categoriesEdit", "config.categories", "");
  }

  @Override
  public void onCompletion(Download download) {
    final String value = categoriesEdit.getValue().trim();
    if (value.isEmpty()) {
      return;
    }
    final String[] categories = value.split(",");
    final String category = download.getAttribute(categoryAttribute);
    for (String c : categories) {
      if (c.trim().equals(category)) {
        download.setForceStart(true);
        break;
      }
    }
  }
}
