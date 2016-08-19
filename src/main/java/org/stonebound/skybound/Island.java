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

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Island implements ConfigurationSerializable {

    private String owner;
    private final int x;
    private final int z;

    public Island(String owner, int x, int z) {
        this.owner = owner;
        this.x = x;
        this.z = z;
    }

    public Island(Map<String, Object> map) {
        this.owner = map.get("owner") == null ? null : map.get("owner").toString();
        this.x = (Integer) map.get("x");
        this.z = (Integer) map.get("z");
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap();

        map.put("owner", getOwner() == null ? "" : getOwner());
        map.put("x", getX());
        map.put("z", getZ());

        return map;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Island) {
            Island other = (Island) o;

            if (x == other.x && z == other.z) {
                return true;
            }
        }

        return false;
    }
}
