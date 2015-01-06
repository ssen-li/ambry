package com.github.ambry.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * An interface for asynchronous, multi-channel network I/O
 */
public interface Selectable {

  /**
   * Begin establishing a socket connection to the given address identified by the given address
   * @param address The address to connect to
   * @param sendBufferSize The send buffer for the socket
   * @param receiveBufferSize The receive buffer for the socket
   * @return A unique id that identifies this connection
   * @throws java.io.IOException If we cannot begin connecting
   */
  public long connect(InetSocketAddress address, int sendBufferSize, int receiveBufferSize) throws IOException;

  /**
   * Begin disconnecting the connection identified by the given id
   */
  public void disconnect(long id);

  /**
   * Wakeup this selector if it is blocked on I/O
   */
  public void wakeup();

  /**
   * Close this selector
   */
  public void close();

  /**
   * Initiate any sends provided, and make progress on any other I/O operations in-flight (connections,
   * disconnections, existing sends, and receives)
   * @param timeout The amount of time to block if there is nothing to do
   * @param sends The new sends to initiate
   * @throws IOException
   */
  public void poll(long timeout, List<NetworkSend> sends) throws IOException;

  /**
   * The list of sends that completed on the last {@link #poll(long, List) poll()} call.
   */
  public List<NetworkSend> completedSends();

  /**
   * The list of receives that completed on the last {@link #poll(long, List) poll()} call.
   */
  public List<NetworkReceive> completedReceives();

  /**
   * The list of connections that finished disconnecting on the last {@link #poll(long, List) poll()}
   * call.
   */
  public List<Long> disconnected();

  /**
   * The list of connections that completed their connection on the last {@link #poll(long, List) poll()}
   * call.
   */
  public List<Long> connected();

}
