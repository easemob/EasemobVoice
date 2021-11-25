package io.agora.agoravoice.im.resp;

import java.io.Serializable;
import java.util.List;

public class CreateChatRoomResp extends CommonResp {
    /*
    "action": "post",
  "application": "22bcffa0-8f86-11e6-9df8-516f6df68c6d",
  "uri": "http://a1.easemob.com/1122161011178276/testapp/chatrooms",
  "entities": [],
  "data": {
    "id": "160663631626241"
  },
  "timestamp": 1632619179693,
  "duration": 0,
  "organization": "1122161011178276",
  "applicationName": "testapp"
     */

    private String action;
    private String application;
    private String uri;
    private String organization;
    private String applicationName;
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
