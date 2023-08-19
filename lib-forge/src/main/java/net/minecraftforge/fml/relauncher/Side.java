package net.minecraftforge.fml.relauncher;

public enum Side {

    /**
     * The client side. Specifically, an environment where rendering capability exists.
     * Usually in the game client.
     */
    CLIENT,
    /**
     * The server side. Specifically, an environment where NO rendering capability exists.
     * Usually on the dedicated server.
     */
    SERVER;

    /**
     * @return If this is the server environment
     */
    public boolean isServer()
    {
        return !isClient();
    }

    /**
     * @return if this is the Client environment
     */
    public boolean isClient()
    {
        return this == CLIENT;
    }
}
