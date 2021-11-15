import nats
from flask import Flask
from flask_restx import Api, Resource, fields

app = Flask(__name__)
api = Api(app, version='1.0', title='Quote API', description='I return a number of friends quotes')

ns = api.namespace('quotes', description='Quote API')

quote = api.model('Quote', {
    'count': fields.Integer(required=True, description='How many quotes to return')
})


async def sync_nats(count):
    nc = await nats.connect("nats://my-nats:4222")
    await nc.publish("queue_b", count)
    sub = nc.subscribe("queue_b")
    data = ""
    for msg in sub.messages:
        data = data + msg.data.decode()
    return data


@ns.route("/<int:count>")
@ns.doc(params={'count': 'An integer"'})
@ns.param('count', 'The amount of quotes to return')
class QuoteClass(Resource):
    async def get(self, count):
        data = sync_nats(count)
        return {
            "quotes": data
        }


if __name__ == '__main__':
    app.run(debug=True)