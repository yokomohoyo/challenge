FROM python:3.8
WORKDIR /code
COPY dependencies.txt .
RUN pip install -r dependencies.txt
COPY  *.py .
CMD [ "python", "./Consumer.py" ]
