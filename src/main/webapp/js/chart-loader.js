function drawChart(table){
  var book_data = table;

  var chart_options = { width: 800,height: 400};
  var chart = new google.visualization.BarChart(document.getElementById('message_chart'));
  chart.draw(book_data, chart_options);
}

function fetchMessageData() {
  fetch("/messagechart")
      .then((response) => {
        return response.json();
      })
      .then((msgJson) => {
        var msgData = new google.visualization.DataTable();
        //define columns for the DataTable instance
        msgData.addColumn('date', 'Date');
        msgData.addColumn('number', 'Message Count');

        for (i = 0; i < msgJson.length; i++) {
            msgRow = [];
            var timestampAsDate = new Date (msgJson[i].timestamp);
            var totalMessages = i + 1;
            //TODO add the formatted values to msgRow array by using JS' push method
            msgRow.push(timestampAsDate);
            msgRow.push(totalMessages);

            msgData.addRow(msgRow);
        }
        drawChart(msgData);
      });
}
