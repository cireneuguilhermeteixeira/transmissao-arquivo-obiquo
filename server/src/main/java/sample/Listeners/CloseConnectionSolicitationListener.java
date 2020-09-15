package sample.Listeners;

import sample.Models.CloseConnectionSolicitation;
import sample.Server;
import org.openspaces.events.EventDriven;
import org.openspaces.events.EventTemplate;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.NotifyType;
import org.openspaces.events.polling.Polling;

@EventDriven
@Polling
@NotifyType(write = true)
@TransactionalEvent
public class CloseConnectionSolicitationListener {
    private Server serverInstance;

    public CloseConnectionSolicitationListener(Server serverInstance){
        this.serverInstance = serverInstance;
    }

    @EventTemplate
    CloseConnectionSolicitation unprocessedData(){
        return new CloseConnectionSolicitation();
    }

    @SpaceDataEvent
    public void eventListener(CloseConnectionSolicitation event){
        serverInstance.onCloseConnectionSolicitationReceived(event);
    }

}
