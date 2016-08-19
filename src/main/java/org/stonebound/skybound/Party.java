/*
 * Copyright (c) 2012, Keeley Hoek
 * Copyright (c) 2016, phit
 * All rights reserved.
 * 
 * Redistribution and use of this software in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * 
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stonebound.skybound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Party implements ConfigurationSerializable {

    private Island island;
    private List<String> members;

    public Party(Island island) {
        this.island = island;
        this.members = new ArrayList<String>();
    }

    public Party(Map<String, Object> map) {
        this.island = (Island) map.get("island");
        this.members = (List) map.get("members");
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap();

        map.put("island", island);
        map.put("members", members);

        return map;
    }

    public Island getIsland() {
        return island;
    }

    public String getLeader() {
        return members.get(0);
    }

    public List<String> getMembers() {
        return members;
    }

    public void changeLeader(String newLeader) {
        members.remove(newLeader);
        members.add(0, newLeader);
    }

    public void addMember(String member) {
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public boolean contains(String player) {
        for (String member : members) {
            if (member.equalsIgnoreCase(player)) {
                return true;
            }
        }

        return false;
    }
}