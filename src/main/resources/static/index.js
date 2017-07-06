(function () {
  function Manager() {

  }

  Manager.prototype.initialized = false;

  Manager.prototype.init = function () {
    var that = this;
    var maxDate = new Date();

    this.loadCurrencies();

    google.charts.load('current', {packages: ['corechart', 'line']});
    google.charts.setOnLoadCallback(function () {
      that.initialized = true;
      that.draw();
    });

    $('.Paremeters__input')
      .datepicker({
        startDate: "01/01/2000",
        format: 'yyyy-mm-dd',
        endDate: maxDate,
        maxViewMode: 3,
        autoclose: true,
        todayHighlight: true
      })
      .on('changeDate', that.draw.bind(that));

    $('#from').datepicker('setDate', this.getDefaultFromDate());
    $('#to').datepicker('setDate', 'now');

    $('.glyphicon').click(this.draw.bind(this));

    $(window).resize(function () {
      clearTimeout(that.resizeTimer);

      that.resizeTimer = setTimeout(that.draw.bind(that), 300);
    });
  };

  Manager.prototype.loadCurrencies = function() {
    var that = this;

    $.get('/api/currencies')
      .then(function(currencies) {
        var base = $("#base-currency");
        var quote = $("#quote-currency");

        currencies.forEach(function(currency) {
          base.append($('<option>')
            .val(currency)
            .text(currency));

          quote.append($('<option>')
            .val(currency)
            .text(currency));
        });

        base.change(that.draw.bind(that));
        quote.change(that.draw.bind(that));
      });
  };

  Manager.prototype.draw = function () {
    function getCurrentPair() {
      return $("#base-currency").val() + '/' + $("#quote-currency").val();
    }

    if (this.initialized) {
      this.setLoading(true);

      $.get('/api/pair/' + getCurrentPair(), {from: this.getFromDate(), to: this.getToDate()})
        .then(function (ratesResult) {
          var data = new google.visualization.DataTable();
          data.addColumn('date', 'Date');
          data.addColumn('number', 'Rate');

          var rows = ratesResult.dates.map(function (element, index) {
            return [new Date(element), ratesResult.values[index]];
          });

          data.addRows(rows);

          var options = {
            vAxis: {
              title: 'Rate'
            },
            legend: {
              position: 'none'
            },
            chartArea: {top: 30, left: '8%', height:'80%', width:'86%'}
          };

          var chart = new google.visualization.LineChart($('#chart').get(0));

          chart.draw(data, options);
        })
        .always(this.setLoading.bind(this, false));
    }
  };

  Manager.prototype.setLoading = function(state) {
    var icon = $('.glyphicon');

    icon.toggleClass('glyphicon-refresh animation-rotate', state);
    icon.toggleClass('glyphicon-transfer', !state);
  };

  Manager.prototype.getDefaultFromDate = function() {
    var fromDate = new Date();
    fromDate.setMonth(fromDate.getMonth() - 1);

    return fromDate;
  };

  Manager.prototype.getFromDate = function() {
    return $('#from').val();
  };

  Manager.prototype.getToDate = function() {
    return $('#to').val();
  };

  var manager = new Manager();
  manager.init();
})();