const express = require('express');
const app = express();
const PORT = process.env.PORT || 3001;
const util = require('util');

let services = [
    { id: 1, name: 'Usługa 1', price: 10 },
    { id: 2, name: 'Usługa 2', price: 20 }
];

app.use(express.json());

app.get('/services', (req, res) => {
    res.json(services);
});

app.post('/services', (req, res) => {
    const newService = { id: services.length + 1, ...req.body };
    services.push(newService);
    res.json(services);
});

app.post('/payments', (req, res) => {
    console.log(util.inspect(req.body, { depth: null, colors: true }));
    res.status(200).send('Płatność zakończona pomyślnie!');
});

app.listen(PORT, () => {
    console.log(`Server listening on port ${PORT}`);
});