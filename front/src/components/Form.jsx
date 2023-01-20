import React, {useState, useEffect} from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import axios from 'axios'
import DataTable from './DataTable'

export default function Form() {

    const [url, setUrl] = useState("")
    const [data, setData] = useState()
    const [keys, setKeys] = useState([])

    const onChange = (e) => {
        setUrl(e.target.value)
    }
    const onClick = async () => {
        setKeys([])
        await axios.get("http://localhost:8080/contents",
            {
                params: {
                url: url
            }
        })
            .then(res => {
                setData(res.data)
        })
    }

    useEffect(() => {
        if (data !== undefined) {
            const key = Object.keys(data)
            
            for (let i = 0; i < key.length; i++){
                setKeys(keys => [...keys, key[i]])
            }   
        }
    }, [data]);
    

    return (
        <div style={
            { textAlign: 'center', marginTop: 50 }
        }>
            <TextField id="standard-basic" label="url 입력" variant="standard"
                sx={{ m: 1, width: '100ch' }}
                onChange={onChange}
            />
            <Button variant="contained"
                onClick={onClick}
            >
                검색
            </Button> 
            <DataTable keys={keys} data={data}></DataTable>
        </div>
    );
}