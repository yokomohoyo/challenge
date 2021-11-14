import asyncio
import nats
import psycopg2
from nats.aio.errors import ErrConnectionClosed, ErrTimeout, ErrNoServers


async def run():
    # NATS Client
    nc = await nats.connect("nats://my-nats:4222")
    sub = await nc.subscribe("queue_a")

    # PGSQL Client
    cs = "dbname=%s user=%s password=%s host=%s port=%s" % ("postgres","postgres","f00M4nCh3w","postgres","5432")
    conn = psycopg2.connect(cs)
    cur = conn.cursor()

    # Initialize table if not there
    cur.execute("CREATE TABLE IF NOT EXISTS queue_a (id serial PRIMARY KEY, body VARCHAR(255) NOT NULL)")
    conn.commit()

    try:
        async for msg in sub.messages:
            print(f"Received a message '{msg.data.decode()}'")
            cur.execute("INSERT INTO queue_a (body) VALUES('{0}')".format(msg.data.decode()))
            conn.commit()
    except ErrTimeout as e:
        print("Timed out connecting to server...")
    except ErrNoServers as en:
        print("Unable to connect to server...")
    except ErrConnectionClosed as ecc:
        print("Connection to server closed...")

if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    loop.run_until_complete(run())
    loop.close()
