package manila.frogmod.mcs;

import java.util.Optional;

/**
 * Created by swordfeng on 16-11-18.
 */
public interface IAPIHandler {
    Optional<JsonMessage> onMessage(JsonMessage msg);
}
