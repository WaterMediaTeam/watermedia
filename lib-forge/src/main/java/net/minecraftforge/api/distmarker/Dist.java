package net.minecraftforge.api.distmarker;

public enum Dist {
    /**
     * The client distribution. This is the game client players can purchase and play.
     * It contains the graphics and other rendering to present a viewport into the game world.
     */
    CLIENT,
    /**
     * The dedicated server distribution. This is the server only distribution available for
     * download. It simulates the world, and can be communicated with via a network.
     * It contains no visual elements of the game whatsoever.
     */
    DEDICATED_SERVER;

    /**
     * @return If this marks a dedicated server.
     */
    public boolean isDedicatedServer()
    {
        return !isClient();
    }

    /**
     * @return if this marks a client.
     */
    public boolean isClient()
    {
        return this == CLIENT;
    }
}
