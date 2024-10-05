package me.srrapero720.watermedia.api.network.patchs;

import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;

public class OneDrivePatch extends AbstractPatch {

    @Override
    public String platform() {
        return "";
    }

    @Override
    public boolean validate(MediaURI source) {
        return false;
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        return null;
    }

    public static class Item {
            private String id;
            private String name;
            private long size;
            @SerializedName("@content.downloadUrl")
            private String url;

            public Item() {}

            public Item(String id, String name, long size, String url) {
                this.id = id;
                this.name = name;
                this.size = size;
                this.url = url;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public long getSize() {
                return size;
            }

            public String getUrl() {
                return url;
            }
        }
}
