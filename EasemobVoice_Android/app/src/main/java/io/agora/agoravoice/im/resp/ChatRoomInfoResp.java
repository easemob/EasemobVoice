package io.agora.agoravoice.im.resp;

import java.util.List;

public class ChatRoomInfoResp extends CommonResp{

    private String action;
    private String application;
    private String uri;
    private String organization;
    private String applicationName;
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data{
        private String id;
        private String affiliations_count;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAffiliations_count() {
            return affiliations_count;
        }

        public void setAffiliations_count(String affiliations_count) {
            this.affiliations_count = affiliations_count;
        }
    }
}
