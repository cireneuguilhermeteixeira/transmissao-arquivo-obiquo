package sample.Listeners;

import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;
import sample.Models.ConnectionSolicitation;
import sample.Server;


@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class ConnectionSolicitationListener {
    private Server serverInstance;

    public ConnectionSolicitationListener(Server serverInstance){
        this.serverInstance = serverInstance;
    }

    @EventTemplate
    ConnectionSolicitation unprocessedData(){
        return new ConnectionSolicitation();
    }

    @SpaceDataEvent
    public void eventListener(ConnectionSolicitation event){
        serverInstance.onConnectionSolicitationReceived(event);
    }
}
