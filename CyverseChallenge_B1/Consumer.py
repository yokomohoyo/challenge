import asyncio
import nats
import psycopg2


async def run():
    # Handle work requests
    async def request_handler(msg):
        cur.execute("SELECT body FROM queue_a ORDER BY id DESC LIMIT {0}".format(msg.data.decode()))
        for quote in cur.fetchall():
            nats.publish("queue_b", "b'{0}'".format(quote))

    # NATS Client
    nc = await nats.connect("nats://my-nats:4222")
    sub = await nc.subscribe("queue_b", request_handler)

    # PGSQL Client
    cs = "dbname=%s user=%s password=%s host=%s port=%s" % ("postgres","postgres","f00M4nCh3w","postgres","5432")
    conn = psycopg2.connect(cs)
    cur = conn.cursor()

if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    loop.run_until_complete(run())
    loop.close()
