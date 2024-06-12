import socket
from _thread import start_new_thread
import time

# Server IP and Port
HOST = '203.234.62.226'
PORT = 10010
PORT2 = 10020

print('>> Server Start with ip :', HOST)
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen(5)

server_socket2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket2.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket2.bind((HOST, PORT2))
server_socket2.listen(5)


def client_thread(connection, client_address, connection2, client_address2):
    try:
        while True:
            client_data = ""
            
            data = connection2.recv(16)
            if not data :
                print("no data")
                break
            print('Received:', data.decode())
            client_data = data.decode(encoding='UTF-8', errors='strict').strip()
            stop_count_down_signal(client_data, connection)
    except Exception as e:
        print(f"Error in client_thread: {e}")
    finally:
        connection.close()
        connection2.close()
        print("Connections closed")


def stop_count_down_signal(client_data, connection): 
    
        
        if (len(client_data)<=5):
            if float(client_data) < 18:    
                connection.send("0\n".encode())
                print("sent: 0")
            else:
                connection.send("1\n".encode())
                print("sent: 1")
        else:
            print("too long data")
            
    
   

while True:
    try:
        print("Waiting for connections...")
        connection, client_address = server_socket.accept()
        connection2, client_address2 = server_socket2.accept()
        print('Connection from', client_address)
        print('Connection from', client_address2)
            

        start_new_thread(client_thread, (connection, client_address, connection2, client_address2))
    except Exception as e:
        print(f"Error accepting connections: {e}")
        time.sleep(1)
