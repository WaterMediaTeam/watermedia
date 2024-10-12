package org.watermedia.core.network.patchs;

import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MediaURI;
import org.watermedia.api.network.URIPatchException;

public class OneDrivePatch extends AbstractPatch {

    @Override
    public String platform() {
        return "";
    }

    @Override
    public boolean active(MediaContext context) {
        return false;
    }

    @Override
    public boolean validate(MediaURI source) {
        return false;
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws URIPatchException {

    }

    @Override
    public void test(MediaContext context, String url) {

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
