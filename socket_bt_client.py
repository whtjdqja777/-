import socket
import time
import random
import bluetooth

bt_address = "98:DA:60:0B:D1:81"
bt_port = 1
sock_host = '203.234.62.226'
sock_port = 10020

def bt_receive_msg(sock):
    buffer = ""
    try:
        while True:
            data = sock.recv(1024).decode()
            print(data)
            if data:
                buffer += data
                if '\n' in data:
                    messages = buffer.split('\n')
                    print("Received data from bluetooth device:", messages[0])
                    break

    except Exception as e:
        print("Failed to receive message by bluetooth:", e)

    return messages[0]

def bt_send_msg(sock, message):
    try:
        sock.send(message.encode('utf-8'))
        print("Sent data to bluetooth device:", message)
    except Exception as e:
        print("Failed to send message:", e)

def bluetoothconnect():
    nearby_devices = bluetooth.discover_devices(duration=8, lookup_names=True, flush_cache=True)

    if bt_address not in [addr for addr, _ in nearby_devices]:
        print("Cannot Search target device")
        return
    print("Target device searched successfully")
    bt_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    bt_sock.connect((bt_address, bt_port))
    print("Bluetooth connection succeeded")
    return bt_sock

def client_program(bt_sock):
    while True:
        try:
            
            print("Ready for receive message from bluetooth device")
            message = bt_receive_msg(bt_sock)
            message = message.strip()
            
            bt_send_msg(bt_sock, message)
            print()
            client_socket.send(message.encode('utf-8'))
            print(f"Sent: {message}")

        except Exception as e:
            print(f"An error occurred: {e}")
            break 

if __name__ == '__main__':
    bt_sock = bluetoothconnect()
    while True:
        print("Attempting to connect to the server...", sock_host, ":", sock_port)
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((sock_host, sock_port))
        print("Connection established.")
        
 
        client_program(bt_sock)