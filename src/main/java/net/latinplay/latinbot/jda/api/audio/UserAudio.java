/*
 * Copyright 2015-2019 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.latinplay.latinbot.jda.api.audio;

import net.latinplay.latinbot.jda.api.entities.User;

/**
 * Represents a packet of User specific audio.
 */
public class UserAudio
{
    protected User user;
    protected short[] audioData;

    public UserAudio( User user,  short[] audioData)
    {
        this.user = user;
        this.audioData = audioData;
    }

    /**
     * The {@link User User} that provided the audio data.
     *
     * @return Never-null {@link User User} object.
     */
    
    public User getUser()
    {
        return user;
    }

    /**
     * Provides 20 Milliseconds of combined audio data in 48KHz 16bit stereo signed BigEndian PCM.
     * <br>Format defined by: {@link AudioReceiveHandler#OUTPUT_FORMAT AudioReceiveHandler.OUTPUT_FORMAT}.
     * <p>
     * The output volume of the data can be modified by the provided {@code `volume`} parameter. {@code `1.0`} is considered to be 100% volume.
     * <br>Going above {@code `1.0`} can increase the volume further, but you run the risk of audio distortion.
     *
     * @param  volume
     *         Value used to modify the "volume" of the returned audio data. 1.0 is normal volume.
     *
     * @return Never-null byte array of PCM data defined by {@link AudioReceiveHandler#OUTPUT_FORMAT AudioReceiveHandler.OUTPUT_FORMAT}
     */
    
    public byte[] getAudioData(double volume)
    {
        return OpusPacket.getAudioData(audioData, volume);
    }
}
