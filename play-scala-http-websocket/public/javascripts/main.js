(function() {
	
	var currentTable  = undefined;
	var ws = undefined;
	
	var tableInputElem = document.getElementById('tableInput');
	var watchTableBtn = document.getElementById('watchTableButton')
	var statusElem = document.getElementById('status');
	
	function setStatus(status) {
		statusElem.innerHTML = status;
	}
	
	function connect(tableId) {
		if (!tableId || (currentTable === tableId)) {
			return;
		}
		
		if (ws) {
			ws.close();	
		}
		currentTable = tableId;
		
		var wsUrl = 'ws://localhost:9000/ws/orders/table/' + tableId
		ws = new WebSocket(wsUrl);
		ws.onmessage = (event) => {
			console.log('Data:', event);
		};
		
		ws.onerror = (event) => {
			console.error('Error:', event);
			setStatus('Error at ' + tableId);
		};

		console.log('WS created', ws);
		setStatus('Connected to ' + tableId);
	}
	
	function watchTable(e) {
		var tableId = tableInputElem.value;
		if (!tableId) return;

		connect(tableId);
	}

	watchTableBtn.onclick = watchTable;
})();