package sample.Listeners;

import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;
import sample.Models.ConnectionSolicitation;
import sample.Models.Device;
import sample.Server;


@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class ChangePositionListener {
    private Server serverInstance;

    public ChangePositionListener(Server serverInstance){
        this.serverInstance = serverInstance;
    }

    @EventTemplate
    Device unprocessedData(){
        return new Device();
    }

    @SpaceDataEvent
    public void eventListener(Device event){
        serverInstance.onChangePositionReceived(event);
    }
}
