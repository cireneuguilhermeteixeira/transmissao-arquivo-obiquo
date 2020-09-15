package sample.Listeners;

import sample.Models.Enviroment;
import sample.Server;
import org.openspaces.events.EventDriven;
import org.openspaces.events.TransactionalEvent;
import org.openspaces.events.adapter.SpaceDataEvent;
import org.openspaces.events.notify.Notify;
import org.openspaces.events.notify.NotifyType;

@EventDriven
@Notify
@NotifyType(write = true)
@TransactionalEvent
public class EnviromentCreationListener {
    private Server serverInstance;

    public EnviromentCreationListener(Server serverInstance) {
        this.serverInstance = serverInstance;
    }

    @SpaceDataEvent
    public void eventListener(Enviroment event){
        serverInstance.onEnviromentCreated(event);
    }
}
