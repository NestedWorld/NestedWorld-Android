package com.nestedworld.nestedworld.data.converter.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.converter.socket.SocketMessageConverter;
import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AvailableMessage;

public class AvailableMessageConverter implements SocketMessageConverter<AvailableMessage, Combat> {

    /*
     * #############################################################################################
     * # SocketMessageConverter<AvailableMessage,Combat>
     * #############################################################################################
     */
    @NonNull
    @Override
    public Combat convert(@NonNull final AvailableMessage source) {
        final Combat combat = new Combat();
        combat.type = source.type;
        combat.combatId = source.combatId;
        combat.origin = source.origin;
        combat.monsterId = source.monsterId;
        combat.opponentPseudo = source.opponentPseudo;

        return combat;
    }
}
