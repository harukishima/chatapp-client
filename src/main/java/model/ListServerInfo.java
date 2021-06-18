package model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListServerInfo {
    @XmlElement(name = "server")
    List<ServerInfo> list;

    public ListServerInfo() {

    }

    public ListServerInfo(List<ServerInfo> list) {
        this.list = list;
    }

    public void setList(List<ServerInfo> list) {
        this.list = list;
    }

    public List<ServerInfo> getList() {
        return list;
    }
}
